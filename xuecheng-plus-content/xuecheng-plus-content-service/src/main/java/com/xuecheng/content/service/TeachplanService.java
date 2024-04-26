package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * @description 课程基本信息管理业务接口
 * @author Mr.M
 * @date 2022/9/6 21:42
 * @version 1.0
 */
public interface TeachplanService {

/**
 * @description 查询课程计划树型结构
 * @param courseId  课程id
 * @return List<TeachplanDto>
 * @author Mr.M
 * @date 2022/9/9 11:13
*/
 public List<TeachplanDto> findTeachplayTree(long courseId);


 /**
  * 保存课程计划（新增/修改）
  *
  * @param teachplan 课程计划
  */
 void saveTeachplan(SaveTeachplanDto teachplan);


 /**
  * @description 删除课程计划信息
  * @param id teachplanId
  * @author xiaoming
  * @date 2023/1/30 13:01
  */
 public List<TeachplanDto> deleteTeachplan(Long id);


 /**
  * @description 上移
  * @param id teachplanId
  * @author xiaoming
  * @date 2023/1/30 13:02
  */
 public void moveup(Long id);

 /**
  * @description 下移
  * @param id teachplId
  * @author xiaoming
  * @date 2023/1/30 13:03
  */
 public void movedown(Long id);

 /**
  * @description 移动课程计划
  * @param teachPlanId 课程计划id
  * @return void
  * @author Mr.M
  * @date 2022/9/9 20:35
  */
 public void moveTeachPlan(Long teachPlanId,String moveType);

 }