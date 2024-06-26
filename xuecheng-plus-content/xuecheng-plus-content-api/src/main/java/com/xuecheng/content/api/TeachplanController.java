package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description 课程计划编辑接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
 @Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
 @RestController
 @RequestMapping("/teachplan")
 public class TeachplanController {

    @Autowired
    TeachplanService teachplanService;

    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId",name = "课程基础Id值",required = true,dataType = "Long",paramType = "path")
    @GetMapping("/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
        return teachplanService.findTeachplayTree(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("")
    public void saveTeachplan(@RequestBody SaveTeachplanDto teachplan) {
        teachplanService.saveTeachplan(teachplan);
    }

    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto) {
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }



    @ApiOperation(value = "课程计划和媒资信解绑")
    @DeleteMapping("/association/media/{teachplanId}/{mediaId}")
    public void deleteMedia(@PathVariable Long teachplanId,@PathVariable String  mediaId) {
        teachplanService.delAassociationMedia(teachplanId,mediaId);
    }



    @ApiOperation("课程计划删除")
    @DeleteMapping("/{id}")
    public List<TeachplanDto> deleteTeachplan(@PathVariable Long id){
        return teachplanService.deleteTeachplan(id);
    }


    @ApiOperation("课程计划上移")
    @PostMapping("/moveup/{id}")
    public void moveup(@PathVariable Long id){
        teachplanService.moveup(id);
    }

    @ApiOperation("课程计划下移")
    @PostMapping("/movedown/{id}")
    public void movedown(@PathVariable Long id){
        teachplanService.movedown(id);
    }


    @ApiOperation(value = "移动课程计划")
    @PostMapping("teachplan/{moveType}/{teachplanId}")
    public void moveTeachPlan(@PathVariable String moveType,@PathVariable Long teachplanId){
        teachplanService.moveTeachPlan(teachplanId,moveType);
    }

}
