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

                if (line.contains("여권번호/ Passport No.")) {
                    passportNumber = lines[index + 2].trim(); // 여권번호는 "Passport No." 다다음 줄에서 추출
                } else if (line.contains("이름/Given names")) {
                    name = lines[index + 1].trim(); // 이름은 "Given names" 다음 줄에서 추출
                } else if (line.contains("성별/Sex")) {
                    sex = lines[index + 1].trim(); // 성별은 "Sex" 다음 줄에서 추출
                } else if (line.contains("국적/ Nationality")) {
                    nationality = lines[index + 1].trim(); // 국적은 "Nationality" 다음 줄에서 추출
                } else if (line.contains("발급일/ Date of issue")) {
                    passportIssueDate = formatDate(lines[index + 1].trim()); // 발급일은 "Date of issue" 다음 줄에서 추출
                    passportExpiryDate = formatDate(lines[index + 2].trim()); // 만료일은 발급일 바로 다음 줄에서 추출
                } else if (line.contains("기간만료일/ Date of expirty")) {
                    dob = formatDate(lines[index + 1].trim()); // 만료일은 "Date of expiry" 다음 줄에서 추출
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

                if (line.contains("외국인등록번호")) {
                    // "외국인등록번호" 줄에서 추출
                    registrationNumber = line.substring(line.indexOf("외국인등록번호") + 7).trim();
                } else if (line.contains("체류자격")) {
                    // "체류자격" 줄에서 추출
                    statusOfResidence = line.substring(line.indexOf("체류자격") + 4).trim();
                } else if (line.contains("발급일자")) {
                    // "발급일자 Issue Date" 문자열 내에서 발급일을 추출
                    registrationIssueDate = line.substring(line.indexOf("발급일자 Issue Date") + 15).trim();
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

            // 텍스트를 줄 단위로 분리
            String[] lines = text.split("\n");
            String socialIntegrationProgramScore = null;

            for (String line : lines) {
                int index = Arrays.asList(lines).indexOf(line);
                if (line.equals("교육")) {
                    socialIntegrationProgramScore = lines[index + 1].trim();
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
                if (line.contains("Total Score")) {
                    sejongInstituteScore = lines[index + 3].substring(0,3).trim();
                }
            }
            if (sejongInstituteScore == null) {
                throw new CommonException(ErrorCode.INVALID_SEJONG_INSTITUTE_FILE, "Missing Fields: sejongInstituteScore");
            } else {
                if(Integer.parseInt(sejongInstituteScore)<108) sejongInstituteScore = "0";
                else if(Integer.parseInt(sejongInstituteScore)<216) sejongInstituteScore = "1";
                else if(Integer.parseInt(sejongInstituteScore)<296) sejongInstituteScore = "2";
                else if(Integer.parseInt(sejongInstituteScore)<401) sejongInstituteScore = "3";
                else if(Integer.parseInt(sejongInstituteScore)<496) sejongInstituteScore = "4";
                else if(Integer.parseInt(sejongInstituteScore)<581) sejongInstituteScore = "5";
                else sejongInstituteScore = "6";
            }
            return sejongInstituteScore;
        } catch (Exception e) {
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
