package cn.edu.ahut.teamwork.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.edu.ahut.teamwork.entity.Student;
import cn.edu.ahut.teamwork.service.ComplexsqlService;
import cn.edu.ahut.teamwork.service.StudentService;

@Controller
@RequestMapping(value="/student")
public class StudentController {
	
	@Autowired
	StudentService studentService;
	
	@Autowired
	ComplexsqlService complexSqlService;
	
	@RequestMapping(value="/findall")
	@ResponseBody
	public Map<String, Object> findall(){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Student> students = studentService.findAll();
			map.put("students", students);
			map.put("success", true);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return map;
	}
	
	@RequestMapping(value="/loginout")
	public String LoginOut(HttpSession session) {
		session.removeAttribute("LOGIN");
		session.removeAttribute("username");
		session.removeAttribute("loginname");
		session.removeAttribute("role");
		return "Login";
	}
	
	@RequestMapping(value="/login")
	@ResponseBody
	public Map<String, Object> login(
			@RequestParam(value="username",required=false)String username,
			@RequestParam(value="password",required=false)String password,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session,Model model
			){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (username == "" || username == null) {
				map.put("success", false);
				map.put("info", "?????????????????????");
				map.put("type", 3);
			}else if (password == "" || password == null) {
				map.put("success", false);
				map.put("info", "????????????????????????");
				map.put("type", 4);
			}else {
				//????????????????????????
				Student student = studentService.findById(username);
				System.out.println("student syso: "+student);
				if (student == null) {
					map.put("success", false);
					map.put("info", "???????????????");
					map.put("username", false);
					map.put("type", 1);
				}
				else if (!student.getPassword().equals(password)) {
					map.put("success", false);
					map.put("info", "????????????");
					map.put("password", false);
					map.put("type", 2);
				}
				else if (student.getPassword().equals(password)) {
					map.put("success", true);
					map.put("info", "????????????");
					map.put("type", 0);
					session.setAttribute("LOGIN", username);
					session.setAttribute("username", username);
					session.setAttribute("loginname", student.getName());
					session.setAttribute("role", "student");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}
	
	@RequestMapping(value="/login1")
	//@ResponseBody
	//	public Map<String, Object> login1(
	public String login1(
			@Param(value="username")String username,
			@Param(value="password")String password,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session,Model model
			){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (username == "" || username == null) {
				map.put("success", false);
				map.put("info", "?????????????????????");
				map.put("type", 3);
			}else if (password == "" || password == null) {
				map.put("success", false);
				map.put("info", "????????????????????????");
				map.put("type", 4);
			}else {
				//????????????????????????
				Student student = studentService.findById(username);
				System.out.println(student);
				if (student == null) {
					map.put("success", false);
					map.put("info", "???????????????");
					map.put("username", false);
					map.put("type", 1);
				}
				else if (!student.getPassword().equals(password)) {
					map.put("success", false);
					map.put("info", "????????????");
					map.put("password", false);
					map.put("type", 2);
				}
				else if (student.getPassword().equals(password)) {
					map.put("success", true);
					map.put("info", "????????????");
					map.put("type", 0);
					//???????????????
					session.setAttribute("LOGIN", username);
					//????????????????????????
					request.setAttribute("username", username);
					request.setAttribute("username1", "1111111");
					//model.addAttribute("username1", username);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		//return map;
		return "StudentMain1";
	}
	
	/**
	 * ??????????????????????????????
	 * @param studentid
	 * @param request
	 * @param response
	 * @param model
	 * @return json
	 */
	@RequestMapping(value="/findstudentbyid")
	@ResponseBody
	public Map<String, Object> findStudentById(
			@Param(value="studentid") String studentid,
			HttpServletRequest request,HttpServletResponse response,Model model) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (studentid == ""  || studentid == null) {
				map.put("code", "100");
				map.put("info", "??????????????????!");
			}
			else {
				Student student = studentService.findById(studentid);
				if (student != null) {
					map.put("code", "200");
					map.put("info", "????????????!");
					map.put("student", student);
				} else {
					map.put("code", "100");
					map.put("info", "????????????!");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * @descript ????????????id???????????????????????????????????????
	 * @author laq
	 * @param courseid
	 * @param pageNum
	 * @param pageSize
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/findStudentByCourseidPageHelper")
	@ResponseBody
	public Map<String, Object> findStudentByCourseidPageHelper(
			@Param(value="courseid") String courseid,
			@RequestParam(value="pageNum", defaultValue="1") Integer pageNum,
			@RequestParam(value="pageSize",defaultValue="5") Integer pageSize,
			HttpServletRequest request,HttpServletResponse response,Model model){
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (courseid == null || courseid == "") {
				map.put("code", "100");
				map.put("info", "??????????????????!");
			} else {
				PageHelper.startPage(pageNum, pageSize);
				List<Student> students = complexSqlService.findStudentByCourseid(courseid);
				if (students == null) {
					map.put("code", "100");
					map.put("info", "??????????????????!");
				} else {
					PageInfo<Student> pageInfo = new PageInfo<Student>(students);
					map.put("code", "200");
					map.put("info", "????????????!");
					map.put("pageInfo", pageInfo);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * @descript ????????????id???????????????????????????
	 * @author laq
	 * @param courseid
	 * @param pageNum
	 * @param pageSize
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/findStudentByCourseid")
	@ResponseBody
	public Map<String, Object> findStudentByCourseid(
			@Param(value="courseid") String courseid,
			//@RequestParam(value="pageNum", defaultValue="1") Integer pageNum,
			//@RequestParam(value="pageSize",defaultValue="5") Integer pageSize,
			HttpServletRequest request,HttpServletResponse response,Model model){
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (courseid == null || courseid == "") {
				map.put("code", "100");
				map.put("info", "??????????????????!");
			} else {
				List<Student> students = complexSqlService.findStudentByCourseid(courseid);
				if (students == null) {
					map.put("code", "100");
					map.put("info", "??????????????????!");
				} else {
					map.put("code", "200");
					map.put("info", "????????????!");
					map.put("students", students);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}
	
	

}
