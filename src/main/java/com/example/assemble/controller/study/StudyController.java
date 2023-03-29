package com.example.assemble.controller.study;

import com.example.assemble.domain.study.StudyVO;
import com.example.assemble.domain.user.UserVO;
import com.example.assemble.service.study.JoinStudyService;
import com.example.assemble.service.study.StudyService;
import com.example.assemble.service.study.StudyTalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class StudyController {
    private final StudyService studyService;
    private final StudyTalkService studyTalkService;
    private final JoinStudyService joinStudyService;

    // 세션 유저 정보 가져오기
    public UserVO getSessionUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        UserVO user = null;
        if(session != null) {
            user = (UserVO)session.getAttribute("user");
        }
        return user;
    }

    @GetMapping("/")
    public String getStudyAll(Model model, HttpServletRequest request) {
        UserVO sessionUser = getSessionUser(request);
        model.addAttribute("userVO", sessionUser);
        model.addAttribute("studyList", studyService.list());
        return "/index";
    }

    // 해당 유저가 가입 되어 있는 스터디 목록
    @GetMapping("/list")
    public String getStudyListByUserId(Model model, HttpServletRequest request) {
        UserVO sessionUser = getSessionUser(request);
        if(sessionUser == null) return "/login";
        model.addAttribute("studyList", joinStudyService.studyList(sessionUser.getUserId()));
        return "/study/list";
    }

    // 스터디 상세 보기
    @GetMapping("/study")
    public void getStudy(StudyVO studyVO) {
        studyVO = studyService.viewDetail(studyVO.getStudyId());
    }

    // 스터디 생성 페이지 이동
    @GetMapping("/add")
    public String add(StudyVO studyVO, UserVO userVO, HttpServletRequest request) {
        UserVO sessionUser = getSessionUser(request);
        if(sessionUser == null) return "/login";
        return "/study/add";
    }
    // 스터디 생성
    @PostMapping("/add")
    public RedirectView add(StudyVO studyVO) {
        studyService.createStudy(studyVO);
        return new RedirectView("/study/list");
    }

    // 스터디 수정 페이지 이동
    @GetMapping("/update")
    public String update(StudyVO studyVO, HttpServletRequest request) {
        UserVO sessionUser = getSessionUser(request);
        if(sessionUser == null) return "/login";
        return "/study/update";
    }
    // 스터디 수정
    @PostMapping("/update")
    public String update(StudyVO studyVO, Model model) {
        studyService.updateStudy(studyVO);

        return "/";
    }

    // 스터디 삭제
    @PostMapping("/delete")
    public RedirectView delete(Long studyId) {
        studyService.deleteStudy(studyId);
        return new RedirectView("/study/list");
    }

    // 스터디톡 대화 내용
    @GetMapping("/talk")
    public void talk(Long studyId, Model model) {
        model.addAttribute("talks", studyTalkService.viewTalkList(studyId));
    }

    // 스터디 검색 페이지
    @GetMapping("/search")
    public void search(StudyVO studyVO) {

    }
    // 스터디 검색(여러 조건 넣어서 검색)
    @PostMapping("/search")
    public void search(StudyVO studyVO, Model model){
        model.addAttribute("studies", studyService.search(studyVO));
    }
}
