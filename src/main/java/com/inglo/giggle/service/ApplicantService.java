package com.inglo.giggle.service;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.domain.Applicant;
import com.inglo.giggle.domain.ApplicantFile;
import com.inglo.giggle.dto.request.*;
import com.inglo.giggle.dto.response.PassportDto;
import com.inglo.giggle.dto.response.RegistrationDto;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.ApplicantFileRepository;
import com.inglo.giggle.repository.ApplicantRepository;
import com.inglo.giggle.utility.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicantService {
    private final ApplicantRepository applicantRepository;
    private final ApplicantFileRepository applicantFileRepository;
    private final ImageUtil imageUtil;
    @Value("${google-api.key}")
    private String googleApiKey;

    @Transactional
    public void updateApplicant(@UserId Long userId, UpdateApplicantDto updateApplicantDto) {
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));

        applicant.updateApplicant(updateApplicantDto);
    }

    public PassportDto extractFromPassport(@UserId Long userId, MultipartFile file) {
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
        ApplicantFile applicantFile = applicantFileRepository.findByApplicant(applicant)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT_FILE));
        String pastPassportFileUrl = null;
        String currentPassportFileUrl = null;
        boolean isPassportFileExist = applicantFile.getPassportFileUrl() != null;
        try{
            if (isPassportFileExist) {
                pastPassportFileUrl = applicantFile.getPassportFileUrl();
            }
            currentPassportFileUrl = imageUtil.uploadPassportImageFile(file, userId);
            applicantFile.uploadPassportFile(currentPassportFileUrl);
        } catch (Exception e) {
            log.error("Passport file upload error", e);
            throw new CommonException(ErrorCode.UPLOAD_FILE_ERROR);
        }
        try {
            String base64Image = imageUtil.encodeImageToBase64(file);
            String jsonResponse = callGoogleVisionApi(base64Image);
            // JSON 응답 파싱
            JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
            JSONObject responseJson = (JSONObject) parser.parse(jsonResponse);
            JSONObject responses = (JSONObject) ((net.minidev.json.JSONArray) responseJson.get("responses")).get(0);

            // fullTextAnnotation 객체 가져오기
            JSONObject fullTextAnnotation = (JSONObject) responses.get("fullTextAnnotation");
            String text = (String) fullTextAnnotation.get("text");

            // 텍스트를 줄 단위로 분리
            String[] lines = text.split("\n");
            String passportNumber = null;
            String name = null;
            String sex = null;
            String dob = null;
            String nationality = null;
            String passportIssueDate = null;
            String passportExpiryDate = null;

            for (String line : lines) {
                int index = Arrays.asList(lines).indexOf(line);

                if (line.matches("^[A-Z0-9]{9,16}$")) {
                    passportNumber = line;
                } else if (line.matches("^(?=.*[A-Z]{2,})(?=.*<.{2,}).+$")) { // 이름
                    // 6번째 인덱스부터 첫 <가 나오기 전까지의 영대문자들을 추출하여 surname으로 설정
                    int startIndex = 5;
                    int endIndex = line.indexOf('<', startIndex);

                    if (endIndex != -1) {
                        String surName = line.substring(startIndex, endIndex).replaceAll("[^A-Z]", "").trim();
                        // surName을 사용할 코드 작성

                        // << 이후 이름 추출
                        String remaining = line.substring(endIndex);
                        String[] nameParts = remaining.split("<+");
                        StringBuilder givenNameBuilder = new StringBuilder();

                        for (int i = 1; i < nameParts.length; i++) { // nameParts[0]는 빈 문자열이므로 무시
                            if (!nameParts[i].isEmpty()) {
                                if (!givenNameBuilder.isEmpty()) {
                                    givenNameBuilder.append(" "); // < 하나가 있으면 띄어쓰기
                                }
                                givenNameBuilder.append(nameParts[i].replaceAll("[^A-Z]", "")); // 이름 부분에서 영대문자만 남김
                            }
                        }
                        name = surName + " " + givenNameBuilder.toString().trim();
                    }
                } else if (line.matches("^(?=.*[A-Z])(?=.*\\d)[A-Z0-9< ]{30,}$") && (passportNumber != null)) {
                    nationality = line.substring(passportNumber.length() + 1, passportNumber.length() + 4);
                    dob = line.substring(passportNumber.length() + 4, passportNumber.length() + 10).trim();
                    sex = line.substring(passportNumber.length() + 12, passportNumber.length() + 13).trim();
                    passportExpiryDate = line.substring(passportNumber.length() + 13, passportNumber.length() + 19).trim();
                    if ((Integer.parseInt(passportExpiryDate.substring(0, 2)) - 10) >= 0) {
                        passportIssueDate = Integer.parseInt(passportExpiryDate.substring(0, 2)) - 10 + passportExpiryDate.substring(2, 6);
                    } else {
                        passportIssueDate = 100 + (Integer.parseInt(passportExpiryDate.substring(0, 2)) - 10) + passportExpiryDate.substring(2, 6);
                    }
                }
            }
            if (passportNumber == null || name == null || sex == null || dob == null || nationality == null || passportIssueDate == null || passportExpiryDate == null) {
                StringBuilder missingFields = new StringBuilder();
                if (passportNumber == null) missingFields.append("passportNumber ");
                if (name == null) missingFields.append("name ");
                if (sex == null) missingFields.append("sex ");
                if (dob == null) missingFields.append("dob ");
                if (nationality == null) missingFields.append("nationality ");
                if (passportIssueDate == null) missingFields.append("passportIssueDate ");
                if (passportExpiryDate == null) missingFields.append("passportExpiryDate ");
                imageUtil.deleteImageFile(currentPassportFileUrl);
                throw new CommonException(ErrorCode.INVALID_PASSPORT_FILE, "Missing Fields: " + missingFields.toString());
            }
            log.info("Passport Number: {}", passportNumber);
            log.info("Name: {}", name);
            log.info("Sex: {}", sex);
            log.info("Date of Birth: {}", dob);
            log.info("Nationality: {}", nationality);
            log.info("Passport Issue Date: {}", passportIssueDate);
            log.info("Passport Expiry Date: {}", passportExpiryDate);
            return PassportDto.builder()
                    .passportNumber(passportNumber)
                    .name(name)
                    .sex(sex)
                    .dateOfBirth(dob)
                    .nationality(nationality)
                    .passportIssueDate(passportIssueDate)
                    .passportExpiryDate(passportExpiryDate)
                    .build();
        } catch (Exception e) {
            log.error("Passport file parsing error", e);
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updatePassport(@UserId Long userId, PassportUpdateDto passportUpdateDto) {
        try {
            Applicant applicant = applicantRepository.findById(userId)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
            applicant.registerPassport(passportUpdateDto);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public RegistrationDto extractFromRegistration(@UserId Long userId, MultipartFile file) {
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
        ApplicantFile applicantFile = applicantFileRepository.findByApplicant(applicant)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT_FILE));
        String pastRegistrationFileUrl = null;
        String currentRegistrationFileUrl = null;
        boolean isRegistrationExist = applicantFile.getRegistrationFileUrl() != null;
        try {
            if (isRegistrationExist) {
                pastRegistrationFileUrl = applicantFile.getRegistrationFileUrl();
            }
            currentRegistrationFileUrl = imageUtil.uploadRegistrationImageFile(file, userId);
            applicantFile.uploadRegistrationFile(currentRegistrationFileUrl);
        } catch (Exception e) {
            imageUtil.deleteImageFile(currentRegistrationFileUrl);
            throw new CommonException(ErrorCode.UPLOAD_FILE_ERROR);
        }
        try {
            String base64Image = imageUtil.encodeImageToBase64(file);
            String jsonResponse = callGoogleVisionApi(base64Image);

            // JSON 응답 파싱
            JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
            JSONObject responseJson = (JSONObject) parser.parse(jsonResponse);
            JSONObject responses = (JSONObject) ((net.minidev.json.JSONArray) responseJson.get("responses")).get(0);
            // fullTextAnnotation 객체 가져오기
            JSONObject fullTextAnnotation = (JSONObject) responses.get("fullTextAnnotation");
            String text = (String) fullTextAnnotation.get("text");

            // 텍스트를 줄 단위로 분리
            String[] lines = text.split("\n");
            String registrationNumber = null;
            String statusOfResidence = null;
            String registrationIssueDate = null;

            for (String line : lines) {
                int index = Arrays.asList(lines).indexOf(line);

                if (line.matches(".*\\d{6}-\\d{7}.*")) {
                    // nnnnnn-nnnnnnn 형식의 등록번호를 포함하는 줄에서 등록번호만 추출
                    registrationNumber = line.replaceAll(".*(\\d{6}-\\d{7}).*", "$1").trim();
                } else if (line.matches(".*[가-힣]{1}[가-힣]{1}\\(D-\\d{1}\\).*")) {
                    // 한글 2자와 (D-숫자1자)로 구성된 체류자격만 추출
                    statusOfResidence = line.replaceAll(".*([가-힣]{1}[가-힣]{1}\\(D-\\d{1}\\)).*", "$1").trim();
                } else if (line.matches(".*(\\d{4}\\.\\d{2}\\.\\d{2}\\.).*")) {
                    // 발급일자만 추출
                    registrationIssueDate = line.replaceAll(".*(\\d{4}\\.\\d{2}\\.\\d{2}\\.).*", "$1").trim();
                }
            }

            if (registrationNumber == null || statusOfResidence == null || registrationIssueDate == null) {
                StringBuilder missingFields = new StringBuilder();
                if (registrationNumber == null) missingFields.append("registrationNumber ");
                if (statusOfResidence == null) missingFields.append("statusOfResidence ");
                if (registrationIssueDate == null) missingFields.append("registrationIssueDate ");
                throw new CommonException(ErrorCode.INVALID_REGISTRATION_FILE, "Missing Fields: " + missingFields.toString());
            } else if (!(statusOfResidence.contains("D-2")) && !(statusOfResidence.contains("D-4"))) {
                throw new CommonException(ErrorCode.INVALID_REGISTRATION_FILE, "체류자격이 D-2 혹은 D-4인 경우에만 가입이 가능합니다.");
            }
            return RegistrationDto.builder()
                    .registrationNumber(registrationNumber)
                    .statusOfResidence(statusOfResidence)
                    .registrationIssueDate(registrationIssueDate)
                    .build();
        } catch (Exception e) {
            log.error("Registration file parsing error", e);
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateRegistration(@UserId Long userId, RegistrationUpdateDto registrationUpdateDto) {
        try {
            Applicant applicant = applicantRepository.findById(userId)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
            applicant.registerRegistration(registrationUpdateDto);
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String extractFromTopik(Long userId, MultipartFile file) {
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
        try {
            String base64Image = imageUtil.encodeImageToBase64(file);
            String jsonResponse = callGoogleVisionApi(base64Image);

            // JSON 응답 파싱
            JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
            JSONObject responseJson = (JSONObject) parser.parse(jsonResponse);
            JSONObject responses = (JSONObject) ((net.minidev.json.JSONArray) responseJson.get("responses")).get(0);
            // fullTextAnnotation 객체 가져오기
            JSONObject fullTextAnnotation = (JSONObject) responses.get("fullTextAnnotation");
            String text = (String) fullTextAnnotation.get("text");

            // 텍스트를 줄 단위로 분리
            String[] lines = text.split("\n");
            String topikScore = null;

            for (String line : lines) {
                // 만약 line이 앞에 숫자 하나와 '급'이라는 문자만 포함하고 있다면 ex) 6급
                if (line.matches("^\\d급$")) {
                    topikScore = line;
                }
            }
            if (topikScore == null) {
                throw new CommonException(ErrorCode.INVALID_TOPIK_FILE, "Missing Fields: " + "topikScore");
            }
            return topikScore;
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateTopik(Long userId, TopikUpdateDto topikUpdateDto) {
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
        applicant.registerTopik(topikUpdateDto);
    }

    public String extractFromSocialIntegrationProgram(Long userId, MultipartFile file) {
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
        try {
            String base64Image = imageUtil.encodeImageToBase64(file);
            String jsonResponse = callGoogleVisionApi(base64Image);

            // JSON 응답 파싱
            JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
            JSONObject responseJson = (JSONObject) parser.parse(jsonResponse);
            JSONObject responses = (JSONObject) ((net.minidev.json.JSONArray) responseJson.get("responses")).get(0);
            // fullTextAnnotation 객체 가져오기
            JSONObject fullTextAnnotation = (JSONObject) responses.get("fullTextAnnotation");
            String text = (String) fullTextAnnotation.get("text");
            log.info(text);
            // 텍스트를 줄 단위로 분리
            String[] lines = text.split("\n");
            String socialIntegrationProgramScore = null;

            for (String line : lines) {
                int index = Arrays.asList(lines).indexOf(line);
                if (line.matches("\\d")) {
                    socialIntegrationProgramScore = line;
                }
            }
            if (socialIntegrationProgramScore == null) {
                throw new CommonException(ErrorCode.INVALID_SOCIAL_INTEGRATION_PROGRAM_FILE, "Missing Fields: socialIntegrationProgramScore" );
            }
            return socialIntegrationProgramScore;
        } catch (Exception e) {
            log.error("Social Integration Program file parsing error", e);
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateSocialIntegrationProgram(Long userId, SocialIntegrationProgramUpdateDto socialIntegrationProgramUpdateDto) {
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
        applicant.registerSocialIntegrationProgram(socialIntegrationProgramUpdateDto);
    }

    public String extractFromSejongInstitute(Long userId, MultipartFile file) {
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Multipart:{}", file);
        try {
            String base64Image = imageUtil.encodeImageToBase64(file);
            String jsonResponse = callGoogleVisionApi(base64Image);

            // JSON 응답 파싱
            JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
            JSONObject responseJson = (JSONObject) parser.parse(jsonResponse);
            JSONObject responses = (JSONObject) ((net.minidev.json.JSONArray) responseJson.get("responses")).get(0);
            // fullTextAnnotation 객체 가져오기
            JSONObject fullTextAnnotation = (JSONObject) responses.get("fullTextAnnotation");
            String text = (String) fullTextAnnotation.get("text");

            // 텍스트를 줄 단위로 분리
            String[] lines = text.split("\n");
            String sejongInstituteScore = null;

            for (String line : lines) {
                int index = Arrays.asList(lines).indexOf(line);
                if (line.matches("\\d{1,3}/800")) {
                    // 이 줄이 nnn/800 형식에 맞는지 확인하고, 맞다면 이곳에서 처리
                    sejongInstituteScore = line;
                }
            }
            if (sejongInstituteScore == null) {
                throw new CommonException(ErrorCode.INVALID_SEJONG_INSTITUTE_FILE, "Missing Fields: sejongInstituteScore");
            } else {
                Integer score = Integer.parseInt(sejongInstituteScore.split("/")[0]);
                if (score < 108) {
                    sejongInstituteScore = "0";
                } else if (score < 216) {
                    sejongInstituteScore = "1";
                } else if (score < 296) {
                    sejongInstituteScore = "2";
                } else if (score < 401) {
                    sejongInstituteScore = "3";
                } else if (score < 496) {
                    sejongInstituteScore = "4";
                } else if (score < 581) {
                    sejongInstituteScore = "5";
                } else {
                    sejongInstituteScore = "6";
                }
            }
            return sejongInstituteScore;
        } catch (Exception e) {
            log.error("Sejong Institute file parsing error", e);
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateSejongInstitute(Long userId, SejongInstituteUpdateDto sejongInstituteUpdateDto) {
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
        applicant.registerSejongInstitute(sejongInstituteUpdateDto);
    }

    private String callGoogleVisionApi(String base64Image) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://vision.googleapis.com/v1/images:annotate?key=" + googleApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestJson = buildJsonRequest(base64Image);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }
    private String buildJsonRequest(String base64Image) {
        return "{"
                + "\"requests\": ["
                + "  {"
                + "    \"image\": { \"content\": \"" + base64Image + "\" },"
                + "    \"features\": [ { \"type\": \"TEXT_DETECTION\" } ]"
                + "  }"
                + "]"
                + "}";
    }
    private String formatDate(String input) {
        // 첫 번째로 나오는 두 자리 숫자를 day로 추출
        String day = input.substring(0, 2).trim();
        // 유일한 영어 대문자를 month로 추출
        String month = input.replaceAll("[^A-Z]", "").trim();
        // 맨 끝 4자리만 year로 추출
        String year = input.substring(input.length()-4).trim();
        return day + " " + month + " " + year;
    }
}
