# 语音呼叫模块需求说明书

## 1. 模块概述与目标

### 1.1 模块定位
语音呼叫模块是HIS系统的公共服务模块，提供语音合成(TTS)、语音播报、叫号通知等功能，服务于门诊叫号、报告通知、紧急寻人、全员广播等业务场景。

### 1.2 业务目标
- 实现统一的语音呼叫服务能力
- 提升患者就医体验（叫号提醒、报告通知）
- 提高医院运营效率（语音寻人、紧急通知）
- 保障医疗安全（危急值语音提醒）
- 支持多终端语音同步播报

### 1.3 用户角色
- 分诊护士
- 检验/影像技师
- 系统管理员
- 医护人员
- 患者

### 1.4 Windows平台支持
本模块需完美支持Windows操作系统部署：
- 支持Windows 10/11 专业版/企业版
- 支持Windows Server 2016/2019/2022
- 支持Windows原生语音引擎(SAPI)
- 支持多声卡/多音频设备输出
- 支持远程桌面音频重定向

---

## 2. 功能清单

### 2.1 语音合成服务(TTS)

#### 2.1.1 TTS引擎配置
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 引擎选择 | 支持多种TTS引擎切换 | 高 |
| 语音库管理 | 语音库安装与配置 | 高 |
| 语速调节 | 语速快慢调节(0.5x-2.0x) | 高 |
| 音量调节 | 音量大小调节(0-100) | 高 |
| 音调调节 | 音调高低调节 | 中 |
| 多语言支持 | 中文/方言/英语支持 | 高 |

#### 2.1.2 TTS引擎类型
```yaml
Windows本地引擎:
  - Microsoft SAPI 5.x (Windows内置)
  - Microsoft Speech Platform
  - 第三方TTS引擎(科大讯飞、百度、阿里云等)

云端TTS服务:
  - 科大讯飞语音合成API
  - 百度语音合成API
  - 阿里云语音合成API
  - 腾讯云语音合成API

引擎优先级:
  1. 本地缓存音频(最高优先级，响应最快)
  2. Windows本地TTS引擎
  3. 云端TTS服务(兜底)
```

#### 2.1.3 语音模板配置
```
语音模板类型:
- 叫号模板: 请{号码}号{患者姓名}到{诊室}诊室就诊
- 报告通知: {患者姓名}，您的{检查类型}报告已完成，请到{窗口}取报告
- 寻人通知: {患者姓名}请速到{位置}，{原因}
- 危急值通知: {科室}{患者姓名}{项目}危急值，请立即处理
- 全员通知: {通知内容}
- 候诊提醒: 当前叫号{号码}号，您前面还有{人数}位
```

### 2.2 门诊叫号服务

#### 2.2.1 叫号功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 诊室叫号 | 医生工作站点击叫号 | 高 |
| 分诊叫号 | 分诊台集中叫号 | 高 |
| 过号重呼 | 过号患者重新呼叫 | 高 |
| 批量叫号 | 批量呼叫待诊患者 | 中 |
| 优先叫号 | 特殊患者优先呼叫 | 高 |
| 叫号记录 | 叫号历史记录查询 | 高 |

#### 2.2.2 叫号播报内容
```
标准叫号播报:
"请{排队号}号患者{患者姓名}，到{诊室号}诊室就诊。"

示例:
"请15号患者张明，到101诊室就诊。"

过号重呼播报:
"请{排队号}号患者{患者姓名}，到{诊室号}诊室就诊。请听到广播后尽快前往。"

复诊叫号播报:
"请{排队号}号患者{患者姓名}，到{诊室号}诊室复诊。"
```

#### 2.2.3 叫号规则配置
```yaml
叫号规则:
  间隔时间: 5秒(两次叫号间隔)
  重复次数: 2次(自动重复叫号)
  语速: 1.0x(正常语速)
  音量: 80(音量百分比)

播报顺序:
  1. 播放提示音(ding-dong)
  2. 播放叫号内容
  3. 等待间隔
  4. 重复播报叫号内容
```

### 2.3 报告语音播报

#### 2.3.1 报告通知功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 检验报告通知 | 检验完成语音通知 | 高 |
| 影像报告通知 | 影像报告完成通知 | 高 |
| 病理报告通知 | 病理报告完成通知 | 中 |
| 取药提醒 | 药房取药语音提醒 | 高 |
| 自助取报告 | 自助机语音引导 | 高 |

#### 2.3.2 报告播报内容
```
检验报告完成播报:
"{患者姓名}您好，您的检验报告已完成，请到检验科3号窗口取报告。"

影像报告完成播报:
"{患者姓名}您好，您的{检查类型}检查报告已完成，请到放射科报告领取处取报告。"

取药提醒播报:
"请{患者姓名}到{药房窗口}号窗口取药。"

自助机引导播报:
"请将您的就诊卡放置在读卡区域。"
"正在查询您的报告信息，请稍候。"
"您的报告已打印，请取走。"
```

### 2.4 语音寻人服务

#### 2.4.1 寻人功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 患者寻人 | 语音呼叫患者到指定位置 | 高 |
| 家属寻人 | 呼叫家属到指定位置 | 高 |
| 医护寻人 | 呼叫医护人员 | 高 |
| 区域播报 | 指定区域语音播报 | 高 |
| 全院广播 | 全院范围语音广播 | 中 |

#### 2.4.2 寻人播报内容
```
患者寻人播报:
"{患者姓名}患者，请听到广播后，速到{位置}，您的{家属关系}正在等候。"

紧急寻人播报:
"紧急通知：{患者姓名}患者，请立即到{位置}{科室}，有紧急事项需要处理。"

家属寻人播报:
"{患者姓名}的家属，请到{位置}{科室}，{患者姓名}正在等候。"

医护寻人播报:
"请{科室}{医生姓名}医生，速到{位置}，有紧急会诊。"
```

### 2.5 全员通知服务

#### 2.5.1 系统全员通知
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 系统公告 | 系统维护、升级通知 | 高 |
| 应急通知 | 紧急事件广播通知 | 高 |
| 会议通知 | 全院会议通知广播 | 中 |
| 消防广播 | 消防演习/紧急疏散 | 高 |
| 天气预警 | 恶劣天气预警通知 | 中 |

#### 2.5.2 全员通知播报内容
```
系统维护通知:
"各位患者、医护人员请注意，医院信息系统将于{时间}进行维护升级，预计时长{时长}，届时相关业务将暂停办理，请提前做好准备。"

应急通知:
"紧急通知：{事件内容}，请各位{相关人员}注意{注意事项}。"

消防疏散:
"各位患者、医护人员请注意，现在进行消防紧急疏散演习，请按照疏散指示标志有序撤离，不要乘坐电梯。"
```

### 2.6 医护人员通知

#### 2.6.1 医护通知功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 危急值通知 | 检验危急值语音提醒 | 高 |
| 急诊呼叫 | 急诊医生护士呼叫 | 高 |
| 会诊通知 | 会诊语音通知 | 高 |
| 手术通知 | 手术相关人员通知 | 高 |
| 科室通知 | 科室内部通知广播 | 高 |

#### 2.6.2 医护通知播报内容
```
危急值通知播报:
"危急值通知：{科室}{床号}床患者{患者姓名}，{检验项目}结果{数值}，请立即处理。"

急诊呼叫播报:
"急诊呼叫：请{科室}{医生姓名}医生，立即到急诊科{诊室}号诊室。"

会诊通知播报:
"会诊通知：请{科室}{医生姓名}医生，于{时间}到{科室}{床号}床参加会诊。"

手术通知播报:
"手术通知：{患者姓名}患者手术即将开始，请相关医护人员到手术室{手术间}号做好准备。"
```

### 2.7 内部重要提醒

#### 2.7.1 提醒功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 用药提醒 | 特殊用药时间提醒 | 高 |
| 治疗提醒 | 治疗/检查时间提醒 | 高 |
| 交班提醒 | 医护交班时间提醒 | 高 |
| 设备提醒 | 设备维护提醒 | 中 |
| 库存提醒 | 药品物资缺货提醒 | 高 |

#### 2.7.2 提醒播报内容
```
用药提醒播报:
"提醒：{床号}床患者需要执行{药品名称}注射，请注意查对。"

治疗提醒播报:
"提醒：{床号}床患者{时间}需要进行{治疗项目}，请做好准备。"

交班提醒播报:
"提醒：现在是{时间}，请各科室做好交接班准备。"

库存预警播报:
"提醒：{药品名称}库存不足，当前库存{数量}，请及时补充。"
```

### 2.8 语音设备管理

#### 2.8.1 设备管理功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 音频设备配置 | 音频输出设备选择 | 高 |
| 音量控制 | 各区域音量独立控制 | 高 |
| 设备状态监控 | 音频设备在线状态 | 高 |
| 设备分组 | 按区域/功能分组 | 高 |
| 播放优先级 | 不同类型播报优先级 | 高 |

#### 2.8.2 设备分组配置
```yaml
设备分组规则:
  门诊大厅组:
    - 大厅主扬声器
    - 挂号窗口音箱
    - 自助机音箱

  诊区组:
    - 候诊区扬声器
    - 分诊台音箱
    - 诊室内音箱

  检验科组:
    - 采血窗口音箱
    - 报告领取处音箱

  药房组:
    - 取药窗口音箱

  病区组:
    - 护士站音箱
    - 走廊扬声器

  公共区域组:
    - 各楼层走廊扬声器
    - 电梯厅扬声器
```

#### 2.8.3 播放优先级
```yaml
播报优先级(数字越小优先级越高):
  1. 紧急通知(消防、应急)
  2. 危急值通知
  3. 急诊呼叫
  4. 普通叫号
  5. 报告通知
  6. 寻人播报
  7. 系统公告
  8. 背景音乐

优先级规则:
  - 高优先级播报可打断低优先级播报
  - 同优先级按时间顺序排队
  - 紧急播报立即执行，无需排队
```

### 2.9 语音日志管理

#### 2.9.1 日志功能
| 功能点 | 描述 | 优先级 |
|--------|------|--------|
| 播报记录 | 所有播报记录存储 | 高 |
| 播报统计 | 播报数据统计分析 | 中 |
| 异常记录 | 播报失败异常记录 | 高 |
| 日志查询 | 历史播报记录查询 | 高 |
| 日志导出 | 日志数据导出 | 中 |

---

## 3. 数据实体定义

### 3.1 核心实体

#### 3.1.1 语音任务 VoiceTask
```sql
CREATE TABLE voice_task (
    id                  VARCHAR(36) PRIMARY KEY,
    task_no             VARCHAR(30) NOT NULL UNIQUE,
    task_type           VARCHAR(20) NOT NULL,

    content             TEXT NOT NULL,
    template_id         VARCHAR(36),
    params              TEXT,

    priority            INT NOT NULL DEFAULT 5,
    target_devices      TEXT,
    target_groups       TEXT,

    status              VARCHAR(20) NOT NULL DEFAULT '待播报',
    play_count          INT DEFAULT 0,
    max_play_count      INT DEFAULT 1,

    scheduled_time      DATETIME,
    play_start_time     DATETIME,
    play_end_time       DATETIME,

    duration            INT,
    error_message       TEXT,

    creator_id          VARCHAR(20),
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP
);

-- 任务类型: CALL_NUMBER(叫号), REPORT_NOTICE(报告通知), 
--         FIND_PERSON(寻人), ALL_NOTICE(全员通知),
--         STAFF_NOTICE(医护通知), REMINDER(提醒)
```

#### 3.1.2 语音模板 VoiceTemplate
```sql
CREATE TABLE voice_template (
    id                  VARCHAR(36) PRIMARY KEY,
    template_code       VARCHAR(30) NOT NULL UNIQUE,
    template_name       VARCHAR(100) NOT NULL,
    template_type       VARCHAR(20) NOT NULL,

    content_template    TEXT NOT NULL,
    params_define       TEXT,

    voice_engine        VARCHAR(20),
    voice_name          VARCHAR(50),
    speed               DECIMAL(3,1) DEFAULT 1.0,
    volume              INT DEFAULT 80,
    pitch               INT DEFAULT 50,

    pre_audio           VARCHAR(200),
    post_audio          VARCHAR(200),

    is_system           TINYINT DEFAULT 0,
    is_enabled          TINYINT DEFAULT 1,

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP
);
```

#### 3.1.3 音频设备 AudioDevice
```sql
CREATE TABLE audio_device (
    id                  VARCHAR(36) PRIMARY KEY,
    device_code         VARCHAR(30) NOT NULL UNIQUE,
    device_name         VARCHAR(100) NOT NULL,
    device_type         VARCHAR(20),

    device_group_id     VARCHAR(36),
    device_group_name   VARCHAR(100),

    windows_device_id   VARCHAR(200),
    ip_address          VARCHAR(50),
    port                INT,

    volume              INT DEFAULT 80,
    is_enabled          TINYINT DEFAULT 1,

    status              VARCHAR(20) DEFAULT '离线',
    last_heartbeat      DATETIME,

    location            VARCHAR(100),
    description         VARCHAR(200),

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP
);
```

#### 3.1.4 设备分组 DeviceGroup
```sql
CREATE TABLE device_group (
    id                  VARCHAR(36) PRIMARY KEY,
    group_code          VARCHAR(30) NOT NULL UNIQUE,
    group_name          VARCHAR(100) NOT NULL,
    parent_id           VARCHAR(36),

    group_type          VARCHAR(20),
    location            VARCHAR(100),

    default_volume      INT DEFAULT 80,

    sort_order          INT,
    is_enabled          TINYINT DEFAULT 1,

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME ON UPDATE CURRENT_TIMESTAMP
);
```

#### 3.1.5 播报日志 VoiceLog
```sql
CREATE TABLE voice_log (
    id                  VARCHAR(36) PRIMARY KEY,
    task_id             VARCHAR(36),
    task_no             VARCHAR(30),
    task_type           VARCHAR(20),

    content             TEXT,

    device_id           VARCHAR(36),
    device_name         VARCHAR(100),
    device_group        VARCHAR(100),

    play_result         VARCHAR(20),
    play_duration       INT,
    error_message       TEXT,

    play_time           DATETIME NOT NULL,

    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

---

## 4. 业务流程

### 4.1 语音播报流程
```
┌─────────────────────────────────────────────────────────────────┐
│                       语音播报流程                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 业务触发  │───>│ 创建任务  │───>│ 任务队列  │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │               │                       │
│                         │               │                       │
│                         ▼               ▼                       │
│   ┌──────────┐    ┌──────────┐    ┌──────────┐                │
│   │ 播放完成  │<───│ 音频播放  │<───│ TTS合成   │                │
│   └──────────┘    └──────────┘    └──────────┘                │
│                         │                                       │
│                         ▼                                       │
│                   ┌──────────┐                                 │
│                   │ 记录日志  │                                 │
│                   └──────────┘                                 │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 叫号播报流程
```
1. 医生/分诊台点击叫号
2. 系统生成叫号语音任务
3. 根据诊室确定播放设备
4. TTS合成叫号语音
5. 播放提示音
6. 播放叫号语音
7. 等待间隔
8. 重复播放(可配置次数)
9. 记录播报日志
10. 更新叫号状态
```

---

## 5. 接口定义

### 5.1 语音服务接口

#### 5.1.1 创建播报任务
```
POST /api/voice/task/create

Request:
{
    "taskType": "CALL_NUMBER",
    "templateCode": "CALL_STANDARD",
    "params": {
        "queueNo": "15",
        "patientName": "张明",
        "roomNo": "101"
    },
    "targetGroups": ["OUTPATIENT_HALL", "CLINIC_AREA"],
    "priority": 5,
    "maxPlayCount": 2
}

Response:
{
    "code": 0,
    "message": "任务创建成功",
    "data": {
        "taskId": "VT202401150001",
        "taskNo": "VT202401150001",
        "content": "请15号患者张明，到101诊室就诊。",
        "estimatedTime": "2024-01-15T10:30:05"
    }
}
```

#### 5.1.2 立即播报
```
POST /api/voice/broadcast/immediate

Request:
{
    "content": "紧急通知：请所有医护人员立即到会议室参加紧急会议。",
    "targetGroups": ["ALL_AREAS"],
    "priority": 1,
    "voiceEngine": "SAPI",
    "voiceName": "Microsoft Huihui"
}

Response:
{
    "code": 0,
    "message": "播报已开始",
    "data": {
        "taskId": "VT202401150002"
    }
}
```

#### 5.1.3 寻人播报
```
POST /api/voice/find-person

Request:
{
    "personName": "张三",
    "personType": "PATIENT",
    "targetLocation": "门诊大厅服务台",
    "reason": "家属等候",
    "targetGroups": ["OUTPATIENT_HALL", "CLINIC_AREA"]
}

Response:
{
    "code": 0,
    "message": "寻人播报已发送"
}
```

### 5.2 设备管理接口

#### 5.2.1 获取设备列表
```
GET /api/voice/device/list?groupId=OUTPATIENT_HALL

Response:
{
    "code": 0,
    "data": [
        {
            "deviceId": "DEV001",
            "deviceCode": "SPEAKER_01",
            "deviceName": "大厅主扬声器",
            "deviceGroup": "门诊大厅",
            "volume": 80,
            "status": "在线",
            "location": "门诊一楼大厅"
        }
    ]
}
```

#### 5.2.2 调整音量
```
POST /api/voice/device/volume

Request:
{
    "deviceId": "DEV001",
    "volume": 90
}

Response:
{
    "code": 0,
    "message": "音量调整成功"
}
```

---

## 6. Windows平台技术方案

### 6.1 Windows语音引擎集成

```csharp
// C# Windows语音服务实现示例
using System.Speech.Synthesis;

public class WindowsTTSService
{
    private SpeechSynthesizer synthesizer;

    public WindowsTTSService()
    {
        synthesizer = new SpeechSynthesizer();
        // 获取已安装的语音
        var voices = synthesizer.GetInstalledVoices();
    }

    public void Speak(string text, string voiceName, int rate, int volume)
    {
        synthesizer.SelectVoice(voiceName);
        synthesizer.Rate = rate; // -10 to 10
        synthesizer.Volume = volume; // 0 to 100
        synthesizer.Speak(text);
    }

    public void SpeakAsync(string text, Action onComplete)
    {
        synthesizer.SpeakCompleted += (s, e) => onComplete?.Invoke();
        synthesizer.SpeakAsync(text);
    }

    public void SetOutputDevice(int deviceIndex)
    {
        synthesizer.SetOutputToDefaultAudioDevice();
        // 或指定设备
        // synthesizer.SetOutputToAudioDevice(deviceIndex);
    }
}
```

### 6.2 多声卡输出支持

```yaml
多声卡配置方案:
  方案一: Windows多音频设备
    - 识别所有音频输出设备
    - 为每个设备创建独立的音频流
    - 使用NAudio库实现多设备同时播放

  方案二: 网络音频终端
    - 使用IP广播设备
    - 通过网络协议传输音频流
    - 支持远程音量控制

  方案三: 混合方案
    - 本地音频设备直接播放
    - 远程区域使用网络音频终端
```

### 6.3 Windows服务部署

```yaml
Windows服务配置:
  服务类型: Windows Service
  启动类型: 自动启动
  运行账户: LocalSystem 或指定账户
  依赖服务: 
    - Windows Audio
    - Windows Audio Endpoint Builder

服务功能:
  - 语音任务队列处理
  - TTS合成服务
  - 音频设备管理
  - 心跳保活
  - 日志记录

服务监控:
  - 服务状态监控
  - 进程资源监控
  - 音频设备状态检测
  - 异常自动重启
```

### 6.4 音频缓存策略

```yaml
音频缓存规则:
  高频播报缓存:
    - 叫号语音按模板预生成缓存
    - 数字0-99单独缓存
    - 常用短语缓存

  缓存目录:
    - Windows: C:\ProgramData\HIS\VoiceCache\
    - 缓存格式: WAV/MP3

  缓存更新:
    - 模板变更时重新生成
    - 定期清理过期缓存
    - 缓存大小限制(1GB)
```

---

## 7. 业务规则与约束

### 7.1 播报规则
| 规则编码 | 规则描述 |
|----------|----------|
| VOICE001 | 紧急播报可打断任何正在进行的播报 |
| VOICE002 | 同设备同一时间只能播放一个任务 |
| VOICE003 | 播报失败需重试最多3次 |
| VOICE004 | 设备离线时播报任务转入待执行队列 |

### 7.2 音量规则
| 时间段 | 默认音量 | 说明 |
|--------|----------|------|
| 07:00-12:00 | 80% | 上午门诊高峰 |
| 12:00-14:00 | 60% | 午休时间 |
| 14:00-18:00 | 80% | 下午门诊 |
| 18:00-22:00 | 50% | 晚间 |
| 22:00-07:00 | 30% | 夜间(仅急诊) |

### 7.3 隐私保护
- 叫号播报患者姓名需脱敏处理(如：张*明)
- 敏感信息不在公共区域播报
- 播报内容记录完整日志

---

## 8. 模块交互关系

### 8.1 上游依赖
- **门诊管理模块**: 叫号触发
- **检验管理模块**: 报告完成通知
- **影像管理模块**: 报告完成通知
- **系统管理模块**: 用户认证、设备管理

### 8.2 下游调用
- 无下游依赖，为其他模块提供服务

---

## 9. 性能与安全要求

### 9.1 性能要求
| 指标 | 要求 |
|------|------|
| TTS合成时间 | < 500ms |
| 播报响应时间 | < 1s |
| 并发播报路数 | >= 32路 |
| 设备在线率 | >= 99% |

### 9.2 安全要求
- 播报内容审核机制
- 敏感信息过滤
- 播报日志完整记录
- 设备访问权限控制