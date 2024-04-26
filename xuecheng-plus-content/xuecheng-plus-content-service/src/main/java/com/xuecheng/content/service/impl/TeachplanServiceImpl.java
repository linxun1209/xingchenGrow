package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * @description 课程计划service接口实现类
 * @author Mr.M
 * @date 2022/9/9 11:14
 * @version 1.0
 */
 @Service
public class TeachplanServiceImpl implements TeachplanService {

  @Autowired
  TeachplanMapper teachplanMapper;


  @Autowired
  TeachplanMediaMapper teachplanMediaMapper;

 @Override
 public List<TeachplanDto> findTeachplayTree(long courseId) {
  return teachplanMapper.selectTreeNodes(courseId);
 }

    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto dto) {
        // 获得教学计划id
        Long id = dto.getId();
        // 修改课程计划
        if (id != null) {
            Teachplan teachplan = teachplanMapper.selectById(id);
            if (teachplan != null) {
                BeanUtils.copyProperties(dto, teachplan);
                teachplanMapper.updateById(teachplan);
            }
        } else { // 新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(dto, teachplan);
            // 取出同父同级别的课程数量
            int count = getTeachplanCount(dto.getCourseId(), dto.getParentid());
            // 计算下默认顺序（新的课程计划的orderby）
            teachplan.setOrderby(count + 1);

            teachplanMapper.insert(teachplan);
        }
    }

    /**
     * 找到同级课程计划的数量
     * @code select count(*) from teachplan where course_id=id and parentid=pid;
     * @param courseId 课程id
     * @param parentId 父级目录id
     * @return 同级课程数量
     */
    private int getTeachplanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        return teachplanMapper.selectCount(queryWrapper);
    }

    @Transactional
    @Override
    public List<TeachplanDto> deleteTeachplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        Long courseId = teachplan.getCourseId();
        if (teachplan == null) {
            throw new XueChengPlusException("无法找到该章节");
        }

        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, id);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new XueChengPlusException("存在子章节，无法删除该章节");
        }


        Long teachplanId = teachplan.getId();
        LambdaQueryWrapper<TeachplanMedia> query = new LambdaQueryWrapper<>();
        query.eq(TeachplanMedia::getTeachplanId, teachplanId);
        Integer mediaCount = teachplanMediaMapper.selectCount(query);
        if (mediaCount > 0) {
            teachplanMediaMapper.delete(query);
        }
        teachplanMapper.deleteById(teachplanId);
        return teachplanMapper.selectTreeNodes(courseId);
    }



   /* @Transactional
    @Override
    public void removeTeachPlan(Long teachPlanId) {
        //取出课程计划
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        if(teachplan == null)
            return;

        //课程id
        Long courseId = teachplan.getCourseId();

        //课程信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //审核状态
        String auditStatus = courseBase.getAuditStatus();

        //只有当课程是未提交时方可删除
        if(!"202002".equals(auditStatus)){
            XueChengPlusException.cast("删除失败，课程审核状态是未提交时方可删除。");
        }

        //删除课程计划
        teachplanMapper.deleteById(teachPlanId);
        //删除课程计划与媒资的关联信息
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplan));


    }

    */

    @Override
    public void moveup(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        Long parentid = teachplan.getParentid();
        Integer targetOrderby = teachplan.getOrderby();
        if (targetOrderby == 1) {
            throw new XueChengPlusException("已经是第一个了，无法继续上移");
        }
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, parentid);
        queryWrapper.lt(Teachplan::getOrderby, targetOrderby);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        if (count >= 1) {
            queryWrapper.eq(Teachplan::getOrderby, targetOrderby - 1);
            Teachplan preTeachplan = teachplanMapper.selectOne(queryWrapper);
            preTeachplan.setOrderby(preTeachplan.getOrderby() + 1);
            teachplanMapper.updateById(preTeachplan);
            teachplan.setOrderby(targetOrderby - 1);
        }
        teachplanMapper.updateById(teachplan);
    }

    @Override
    public void movedown(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        Long parentid = teachplan.getParentid();
        Integer targetOrderby = teachplan.getOrderby();
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, parentid);
        queryWrapper.gt(Teachplan::getOrderby, targetOrderby);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        if (count == 0) {
            throw new XueChengPlusException("已经是最后一个了，无法继续下移");
        } else {
            queryWrapper.eq(Teachplan::getOrderby, targetOrderby + 1);
            Teachplan preTeachplan = teachplanMapper.selectOne(queryWrapper);
            preTeachplan.setOrderby(preTeachplan.getOrderby() - 1);
            teachplanMapper.updateById(preTeachplan);
            teachplan.setOrderby(targetOrderby + 1);
        }
        teachplanMapper.updateById(teachplan);
    }


    @Override
    public void moveTeachPlan(Long teachPlanId,String moveType) {

        //课程计划
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);

        //查询同级别的课程计划
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,teachplan.getCourseId()).eq(Teachplan::getParentid,teachplan.getParentid());

        List<Teachplan> teachplans = teachplanMapper.selectList(queryWrapper);

        //如果同级别只有一个课程计划，什么也不处理
        if(teachplans.size()<=1){
            return;
        }


        //根据移动类型进行排序
        if(moveType.equals("moveup")){//上移，找到比当前计划小的，交换位置
            //降序，先找到当前计划，下一个就是要和他交换位置的计划
            Collections.sort(teachplans, new Comparator<Teachplan>() {
                @Override
                public int compare(Teachplan o1, Teachplan o2) {
                    return o2.getOrderby() - o1.getOrderby();
                }
            });
        }else{
            //升序
            Collections.sort(teachplans, new Comparator<Teachplan>() {
                @Override
                public int compare(Teachplan o1, Teachplan o2) {
                    return o1.getOrderby() - o2.getOrderby();
                }
            });
        }

        //找到当前计划
        Teachplan one =null;
        Teachplan two =null;
        Iterator<Teachplan> iterator = teachplans.iterator();
        while (iterator.hasNext()){
            Teachplan next = iterator.next();
            if(next.getId().equals(teachplan.getId())){
                one = next;
                try {
                    Teachplan next1 = iterator.next();
                    two = next1;
                } catch (Exception e) {

                }
            }
        }

        swapTeachplan(one,two);

    }

    //交换位置
    private void swapTeachplan(Teachplan left,Teachplan right){
        if(left==null || right==null){
            return ;
        }
        Integer orderby_left = left.getOrderby();
        Integer orderby_right = right.getOrderby();
        left.setOrderby(orderby_right);
        right.setOrderby(orderby_left);
        teachplanMapper.updateById(left);
        teachplanMapper.updateById(right);
    }


}