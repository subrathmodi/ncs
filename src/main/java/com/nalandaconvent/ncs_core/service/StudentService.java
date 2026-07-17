package com.nalandaconvent.ncs_core.service;

import com.nalandaconvent.ncs_core.dto.AdmissionRequest;
import com.nalandaconvent.ncs_core.entity.AcademicSession;
import com.nalandaconvent.ncs_core.entity.Student;
import com.nalandaconvent.ncs_core.entity.StudentSessionMapping;
import com.nalandaconvent.ncs_core.repository.AcademicSessionRepository;
import com.nalandaconvent.ncs_core.repository.StudentRepository;
import com.nalandaconvent.ncs_core.repository.StudentSessionMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AcademicSessionRepository academicSessionRepository;

    @Autowired
    private StudentSessionMappingRepository studentSessionMappingRepository;

    @Transactional
    public Student processAdmission(AdmissionRequest request) {

        // 1. Resolve target Academic Session reference
        AcademicSession session = academicSessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Target Academic Session ID not found."));

        // 2. Build full core profile using user's compliance model properties
        Student student = new Student();
        student.setSchoolRegNo(request.getSchoolRegNo().trim());
        student.setDateOfAdmission(request.getDateOfAdmission());
        student.setAdmissionType(request.getAdmissionType());
        student.setAdmissionScheme(request.getAdmissionScheme());

        student.setName(request.getName().trim());
        student.setFatherName(request.getFatherName().trim());
        student.setMotherName(request.getMotherName().trim());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setGender(request.getGender());
        student.setReligion(request.getReligion());
        student.setCategory(request.getCategory());
        student.setFatherContactNo(request.getFatherContactNo().trim());
        student.setFullAddress(request.getFullAddress().trim());

        student.setClassOfAdmission(request.getClassOfAdmission());
        student.setMedium(request.getMedium());
        student.setStream(request.getStream());

        student.setChildIdNo(request.getChildIdNo());
        student.setFamilyIdNo(request.getFamilyIdNo());
        student.setStudentAadharNo(request.getStudentAadharNo());
        student.setStudentSssmidNo(request.getStudentSssmidNo());
        student.setPenNo(request.getPenNo());
        student.setApaarIdNo(request.getApaarIdNo());

        student.setBankAccountNo(request.getBankAccountNo());
        student.setIfscCode(request.getIfscCode());
        student.setNameOfBank(request.getNameOfBank());

        student.setCurrentStatus("ACTIVE");

        // Save base details profile
        Student savedStudent = studentRepository.save(student);

        // 3. Inject matching tracking cluster row mapping entry
        StudentSessionMapping mapping = new StudentSessionMapping();
        mapping.setStudent(savedStudent);
        mapping.setAcademicSession(session);
        // Track active class cluster using dynamic field values
        mapping.setCurrentClass(request.getClassOfAdmission());
        mapping.setRollNumber(request.getRollNumber());

        studentSessionMappingRepository.save(mapping);

        return savedStudent;
    }


    public List<StudentSessionMapping> getClusterDirectory(Long sessionId, String currentClass) {
        return studentSessionMappingRepository.findByAcademicSessionIdAndCurrentClass(sessionId, currentClass);
    }
}