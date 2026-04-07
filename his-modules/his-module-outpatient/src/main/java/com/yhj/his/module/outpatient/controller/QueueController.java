package com.yhj.his.module.outpatient.controller;

import com.yhj.his.common.core.domain.Result;
import com.yhj.his.module.outpatient.dto.CallPatientRequest;
import com.yhj.his.module.outpatient.entity.Queue;
import com.yhj.his.module.outpatient.service.QueueService;
import com.yhj.his.module.outpatient.vo.QueueInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 分诊排队Controller
 */
@Tag(name = "分诊排队", description = "叫号、排队、过号等接口")
@RestController
@RequestMapping("/api/outpatient/v1/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @Operation(summary = "叫号", description = "医生叫号")
    @PostMapping("/call")
    public Result<Queue> callPatient(@Valid @RequestBody CallPatientRequest request) {
        Queue queue = queueService.callPatient(request);
        return Result.success("叫号成功", queue);
    }

    @Operation(summary = "根据ID查询排队信息", description = "根据排队ID查询排队信息")
    @GetMapping("/get/{id}")
    public Result<Queue> getQueueById(
            @Parameter(description = "排队ID") @PathVariable String id) {
        Queue queue = queueService.getQueueDetail(id);
        return Result.success(queue);
    }

    @Operation(summary = "获取诊室排队信息", description = "获取当前排队情况")
    @GetMapping("/info")
    public Result<QueueInfoVO> getQueueInfo(
            @Parameter(description = "医生ID") @RequestParam String doctorId,
            @Parameter(description = "日期") @RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        QueueInfoVO result = queueService.getQueueInfo(doctorId, date);
        return Result.success(result);
    }

    @Operation(summary = "查询等候列表", description = "查询医生当日等候列表")
    @GetMapping("/waiting")
    public Result<List<Queue>> listWaitingQueue(
            @Parameter(description = "医生ID") @RequestParam String doctorId,
            @Parameter(description = "日期") @RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        List<Queue> result = queueService.listWaitingQueue(doctorId, date);
        return Result.success(result);
    }

    @Operation(summary = "查询医生当日排队列表", description = "查询医生当日所有排队记录")
    @GetMapping("/list")
    public Result<List<Queue>> listDoctorQueue(
            @Parameter(description = "医生ID") @RequestParam String doctorId,
            @Parameter(description = "日期") @RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        List<Queue> result = queueService.listDoctorQueue(doctorId, date);
        return Result.success(result);
    }

    @Operation(summary = "开始就诊", description = "开始就诊")
    @PostMapping("/start/{id}")
    public Result<Queue> startVisit(
            @Parameter(description = "排队ID") @PathVariable String id,
            @Parameter(description = "医生ID") @RequestParam String doctorId,
            @Parameter(description = "诊室") @RequestParam(required = false) String clinicRoom) {
        Queue queue = queueService.startVisit(id, doctorId, clinicRoom);
        return Result.success("开始就诊", queue);
    }

    @Operation(summary = "结束就诊", description = "结束当前患者就诊")
    @PostMapping("/end/{id}")
    public Result<Queue> endVisit(
            @Parameter(description = "排队ID") @PathVariable String id) {
        Queue queue = queueService.endVisit(id);
        return Result.success("就诊结束", queue);
    }

    @Operation(summary = "过号处理", description = "将患者标记为过号")
    @PostMapping("/pass/{id}")
    public Result<Queue> markAsPassed(
            @Parameter(description = "排队ID") @PathVariable String id) {
        Queue queue = queueService.markAsPassed(id);
        return Result.success("过号处理成功", queue);
    }

    @Operation(summary = "复诊入队", description = "将过号患者重新入队")
    @PostMapping("/requeue/{id}")
    public Result<Queue> requeue(
            @Parameter(description = "排队ID") @PathVariable String id) {
        Queue queue = queueService.requeue(id);
        return Result.success("复诊入队成功", queue);
    }

    @Operation(summary = "获取当前就诊患者", description = "获取医生当前就诊患者")
    @GetMapping("/current")
    public Result<Queue> getCurrentPatient(
            @Parameter(description = "医生ID") @RequestParam String doctorId,
            @Parameter(description = "日期") @RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return queueService.getCurrentPatient(doctorId, date)
                .map(Result::success)
                .orElse(Result.error("当前无就诊患者"));
    }

    @Operation(summary = "统计等候人数", description = "统计医生当日等候人数")
    @GetMapping("/count/waiting")
    public Result<Integer> countWaiting(
            @Parameter(description = "医生ID") @RequestParam String doctorId,
            @Parameter(description = "日期") @RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        int count = queueService.countWaiting(doctorId, date);
        return Result.success(count);
    }

    @Operation(summary = "统计过号人数", description = "统计医生当日过号人数")
    @GetMapping("/count/passed")
    public Result<Integer> countPassed(
            @Parameter(description = "医生ID") @RequestParam String doctorId,
            @Parameter(description = "日期") @RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        int count = queueService.countPassed(doctorId, date);
        return Result.success(count);
    }

    @Operation(summary = "设置优先级", description = "设置患者排队优先级")
    @PostMapping("/priority/{id}")
    public Result<Queue> setPriority(
            @Parameter(description = "排队ID") @PathVariable String id,
            @Parameter(description = "优先级") @RequestParam Integer priority) {
        Queue queue = queueService.setPriority(id, priority);
        return Result.success("设置成功", queue);
    }

    @Operation(summary = "下一个患者", description = "获取下一个待诊患者")
    @GetMapping("/next")
    public Result<Queue> nextPatient(
            @Parameter(description = "医生ID") @RequestParam String doctorId,
            @Parameter(description = "日期") @RequestParam(required = false) LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        Queue queue = queueService.nextPatient(doctorId, date);
        if (queue == null) {
            return Result.error("无待诊患者");
        }
        return Result.success(queue);
    }
}