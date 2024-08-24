package com.inglo.giggle.service;

import com.inglo.giggle.annotation.UserId;
import com.inglo.giggle.domain.Applicant;
import com.inglo.giggle.domain.ApplicantFile;
import com.inglo.giggle.domain.User;
import com.inglo.giggle.dto.request.UpdateApplicantDto;
import com.inglo.giggle.exception.CommonException;
import com.inglo.giggle.exception.ErrorCode;
import com.inglo.giggle.repository.ApplicantFileRepository;
import com.inglo.giggle.repository.ApplicantRepository;
import com.inglo.giggle.repository.UserRepository;
import com.inglo.giggle.utility.ImageUtil;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ApplicantService {
    private final UserRepository userRepository;
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

    @Transactional
    public void updatePassport(@UserId Long userId, MultipartFile file) {
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
            imageUtil.deleteImageFile(currentPassportFileUrl);
            throw new CommonException(ErrorCode.UPLOAD_FILE_ERROR);
        }
        try {
            String base64Image = imageUtil.encodeImageToBase64(file);
            String jsonResponse = callGoogleVisionApi(base64Image);

            // JSON 응답 파싱
            JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
            JSONObject responseJson = (JSONObject) parser.parse(jsonResponse);
            JSONObject responses = (JSONObject) ((net.minidev.json.JSONArray) responseJson.get("responses")).get(0);
            JSONObject textAnnotation = (JSONObject) ((net.minidev.json.JSONArray) responses.get("textAnnotations")).get(0);
            String description = (String) textAnnotation.get("description");

            String[] lines = description.split("\n");
            String passportNumber = null;
            StringBuilder name = null;
            String sex = null;
            String dob = null;
            String nationality = null;
            String passportIssueDate = null;
            String passportExpiryDate = null;

            for (String line : lines) {
                if (line.contains("Passport No.")) {
                    passportNumber = line.substring(line.indexOf("\n") + 1).trim(); // "Passport No." 다음 줄에서 여권 번호 추출
                } else if (line.contains("Given names")) {
                    name = new StringBuilder(line.substring(line.indexOf("\n") + 1).trim()); // "Given names" 다음 줄에서 이름 추출
                } else if (line.contains("Surname") && name != null) {
                    name.append(line.substring(line.indexOf("\n") + 1).trim()); // "Surname" 다음 줄에서 성 추출
                } else if (line.contains("Sex")) {
                    sex = line.substring(line.indexOf("\n") + 1).trim();
                } else if (line.contains("Date of birth")) {
                    dob = line.substring(line.indexOf("\n") + 1).trim(); // "Date of birth" 다음 줄에서 생년월일 추출
                } else if (line.contains("Nationality")) {
                    nationality = line.substring(line.indexOf("\n") + 1).trim(); // "Nationality" 다음 줄에서 국적 추출
                } else if (line.contains("Date of issue")) {
                    passportIssueDate = line.substring(line.indexOf("\n") + 1).trim(); // "Date of issue" 다음 줄에서 발급일 추출
                } else if (line.contains("Date of expiry")) {
                    passportExpiryDate = line.substring(line.indexOf("\n") + 1).trim(); // "Date of expiry" 다음 줄에서 만료일 추출
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
            else {
                applicant.registerPassport(passportNumber, name.toString(), sex, dob, nationality, passportIssueDate, passportExpiryDate);
                if(isPassportFileExist) {
                    imageUtil.deleteImageFile(pastPassportFileUrl);
                }
            }
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateRegistration(@UserId Long userId, MultipartFile file) {
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
            JSONObject textAnnotation = (JSONObject) ((net.minidev.json.JSONArray) responses.get("textAnnotations")).get(0);
            String description = (String) textAnnotation.get("description");

            String[] lines = description.split("\n");
            String registrationNumber = null;
            String statusOfResidence = null;
            String registrationIssueDate = null;

            for (String line : lines) {
                if (line.contains("외국인") && line.contains("-")) {
                    // 해당 줄에서 "123456-1234567" 형식의 외국인 등록번호 찾기
                    Pattern pattern = Pattern.compile("\\b\\d{6}-\\d{7}\\b"); // "123456-1234567" 형식을 찾습니다.
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        registrationNumber = matcher.group(); // 첫 번째 일치하는 번호가 외국인 등록번호입니다.
                    }
                } else if (line.contains("Status of residence")) {
                    statusOfResidence = line.substring(line.indexOf("\n") + 1).trim(); // "Status of residence" 다음 줄에서 체류 상태 추출
                } else if (line.contains("Date of issue")) {
                    registrationIssueDate = line.substring(line.indexOf("\n") + 1).trim(); // "Date of issue" 다음 줄에서 발급일 추출
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
            } else {
                applicant.registerRegistration(registrationNumber, statusOfResidence, registrationIssueDate);
                if(isRegistrationExist) {
                    imageUtil.deleteImageFile(pastRegistrationFileUrl);
                }
            }

        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateTopik(Long userId, MultipartFile file) {
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
        try {
            String base64Image = imageUtil.encodeImageToBase64(file);
            String jsonResponse = callGoogleVisionApi(base64Image);

            // JSON 응답 파싱
            JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
            JSONObject responseJson = (JSONObject) parser.parse(jsonResponse);
            JSONObject responses = (JSONObject) ((net.minidev.json.JSONArray) responseJson.get("responses")).get(0);
            JSONObject textAnnotation = (JSONObject) ((net.minidev.json.JSONArray) responses.get("textAnnotations")).get(0);
            String description = (String) textAnnotation.get("description");

            String[] lines = description.split("\n");
            String topikScore = null;

            for (String line : lines) {
                // TODO: 토픽 증명서에서 필요한 정보 추출
            }

            if (topikScore == null) {
                StringBuilder missingFields = new StringBuilder();
                // TODO : 필요한 정보가 없을 때 처리
                throw new CommonException(ErrorCode.INVALID_TOPIK_FILE, "Missing Fields: " + missingFields.toString());
            } else {
                applicant.registerTopik(topikScore);
            }
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateSocialIntegrationProgram(Long userId, MultipartFile file) {
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
        try {
            String base64Image = imageUtil.encodeImageToBase64(file);
            String jsonResponse = callGoogleVisionApi(base64Image);

            // JSON 응답 파싱
            JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
            JSONObject responseJson = (JSONObject) parser.parse(jsonResponse);
            JSONObject responses = (JSONObject) ((net.minidev.json.JSONArray) responseJson.get("responses")).get(0);
            JSONObject textAnnotation = (JSONObject) ((net.minidev.json.JSONArray) responses.get("textAnnotations")).get(0);
            String description = (String) textAnnotation.get("description");

            String[] lines = description.split("\n");
            String socialIntegrationProgramScore = null;

            for (String line : lines) {
                //TODO: 사회통합프로그램 증명서에서 필요한 정보 추출
            }
            if (socialIntegrationProgramScore == null) {
                StringBuilder missingFields = new StringBuilder();
                //TODO: 필요한 정보가 없을 때 처리
                throw new CommonException(ErrorCode.INVALID_SOCIAL_INTEGRATION_PROGRAM_FILE, "Missing Fields: " + missingFields.toString());
            } else {
                applicant.registerSocialIntegrationProgram(socialIntegrationProgramScore);
            }
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateSejongInstitute(Long userId, MultipartFile file) {
        Applicant applicant = applicantRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_APPLICANT));
        try {
            String base64Image = imageUtil.encodeImageToBase64(file);
            String jsonResponse = callGoogleVisionApi(base64Image);

            // JSON 응답 파싱
            JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
            JSONObject responseJson = (JSONObject) parser.parse(jsonResponse);
            JSONObject responses = (JSONObject) ((net.minidev.json.JSONArray) responseJson.get("responses")).get(0);
            JSONObject textAnnotation = (JSONObject) ((net.minidev.json.JSONArray) responses.get("textAnnotations")).get(0);
            String description = (String) textAnnotation.get("description");

            String[] lines = description.split("\n");
            String sejongInstituteScore = null;

            for (String line : lines) {
                //TODO: 세종학당 증명서에서 필요한 정보 추출
            }
            if (sejongInstituteScore == null) {
                StringBuilder missingFields = new StringBuilder();
                //TODO: 필요한 정보가 없을 때 처리
                throw new CommonException(ErrorCode.INVALID_SEJONG_INSTITUTE_FILE, "Missing Fields: " + missingFields.toString());
            } else {
                applicant.registerSejongInstitute(sejongInstituteScore);
            }
        } catch (Exception e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
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
}
