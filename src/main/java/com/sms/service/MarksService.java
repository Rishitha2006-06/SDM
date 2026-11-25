package com.sms.service;

import com.sms.dto.MarksRecordDto;
import com.sms.model.Course;
import com.sms.model.ExamType;
import com.sms.model.Faculty;
import com.sms.model.Marks;
import com.sms.model.Student;
import com.sms.repository.CourseRepository;
import com.sms.repository.FacultyRepository;
import com.sms.repository.MarksRepository;
import com.sms.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MarksService {

    private final MarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;

    public MarksService(MarksRepository marksRepository,
                        StudentRepository studentRepository,
                        CourseRepository courseRepository,
                        FacultyRepository facultyRepository) {
        this.marksRepository = marksRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
    }

    @Transactional
    public Marks saveMarks(MarksRecordDto dto, Long facultyId) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + dto.getStudentId()));
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + dto.getCourseId()));
        Faculty faculty = facultyId != null ? facultyRepository.findById(facultyId).orElse(null) : null;

        Optional<Marks> existing = marksRepository.findByStudentAndCourseAndExamType(student, course, dto.getExamType());
        Marks marks;
        if (existing.isPresent()) {
            marks = existing.get();
            marks.setMarksObtained(dto.getMarksObtained());
            marks.setMaxMarks(dto.getMaxMarks());
            marks.setGrade(Marks.calculateGrade(dto.getMarksObtained(), dto.getMaxMarks()));
            marks.setRemarks(dto.getRemarks());
            if (faculty != null) marks.setEvaluatedBy(faculty);
            if (dto.getExamDate() != null) marks.setExamDate(dto.getExamDate());
        } else {
            marks = new Marks(student, course, dto.getExamType(), dto.getMarksObtained(),
                    dto.getMaxMarks(), Marks.calculateGrade(dto.getMarksObtained(), dto.getMaxMarks()),
                    dto.getRemarks(), faculty, dto.getExamDate() != null ? dto.getExamDate() : LocalDate.now());
        }

        return marksRepository.save(marks);
    }

    public List<MarksRecordDto> getStudentMarks(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return marksRepository.findByStudent(student).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<MarksRecordDto> getCourseMarks(Long courseId, ExamType examType) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        List<Marks> list = (examType != null)
                ? marksRepository.findByCourseAndExamType(course, examType)
                : marksRepository.findByCourse(course);
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteMarks(Long id) {
        marksRepository.deleteById(id);
    }

    private MarksRecordDto mapToDto(Marks m) {
        MarksRecordDto dto = new MarksRecordDto();
        dto.setId(m.getId());
        dto.setStudentId(m.getStudent().getId());
        dto.setStudentRollNo(m.getStudent().getRollNumber());
        dto.setStudentName(m.getStudent().getFullName());
        dto.setCourseId(m.getCourse().getId());
        dto.setCourseCode(m.getCourse().getCourseCode());
        dto.setCourseName(m.getCourse().getCourseName());
        dto.setExamType(m.getExamType());
        dto.setMarksObtained(m.getMarksObtained());
        dto.setMaxMarks(m.getMaxMarks());
        dto.setGrade(m.getGrade());
        dto.setRemarks(m.getRemarks());
        dto.setExamDate(m.getExamDate());
        return dto;
    }
}
