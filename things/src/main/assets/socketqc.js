"use strict"

//获取WebSocket服务器地址
var Port = location.port; //获取端口号 8080
var Host = location.hostname; //获取主机名 例如：192.168.0.1
var Server = "ws://" + Host + ":" + (parseInt(Port) + 1);

var Vm = new Vue({
    el: '#root',
    data: {
        consoleData: [], // 控制台日志
        messageData: [], // 消息记录
        instance: WebSocket, // ws instance
        address: Server,
        alert: {
            class: 'success',
            state: false,
            content: '',
            timer: undefined
        },
        content: '',
        heartBeatSecond: 1,
        heartBeatContent: 'PING',
        autoSend: false,
        autoTimer: undefined,
        sendClean: false,
        recvClean: false,
        recvDecode: false,
        connected: false,
        recvPause: false,
		
		//发送的指令
		sendCmd: '',
		
		//用户登录
		username: '',
		password: '',
		token: '',
		
		//仪表运行状态
		respond: '',
		isRun: false,
		sysTime: '',
		currentTime: 0,
		progressRate: 0,
		statusMsg: '',
		errorMsg: '',
		startTime: '',
		endTime: '',
		deviceList: '',
		webServiceFlag: true,
		workType: '',
		workFrom: '',
		tempBox: 0,
		waterType: 0,
		sampleType: 0,
		strSpecimen: '',
		concentration: 0,
		waterVolumeNow: 0,
		reagentVolumeNow: 0,
		startSupplySamples: false,
		startSupplySamplesTime: 0,
		//状态显示
		loading: '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>',
		connecting: '<div class="float-right spinner-grow text-success spinner-grow-sm" role="status"><span class="sr-only">Loading...</span></div>',
		
		//数据查询
		perPageTask: 10,
		currentPageTask: 1,
		itemsTask: 0,
		perPageRecord: 10,
		currentPageRecord: 1,
		itemsRecord: 0,
		perPageAlert: 10,
		currentPageAlert: 1,
		itemsAlert: 0,
		chartData: 0,
		chartTime: 0,
		
		//系统设置
		//定时任务
		date: '',
		time: '',
		nextStartTime: 0,
		startCycle: 0,
		numberTimes: 0,
		startWaterType: 0,
		startSampleType: 0,
		//系统参数
		deviceList: '',
		BAUD_RATE: 9600,
		MODBUS_ADDR: 3,
		version: '1.0',
		//基本设置
		supplySamplesTime: 0,
		waterStepVolume: 0,
		reagentStepVolume: 0,
		mixedTime: 0,
		//氨氮参数
		NH3Volume: 0,
		NH3SampleA: 0,
		NH3SampleB: 0,
		NH3SampleC: 0,
		NH3SampleO: 0,
		NH3AddValume: 0,
		NH3AddMul: 0,
		NH3AddType: 0,
		//总磷参数
		TPVolume: 0,
		TPSampleA: 0,
		TPSampleB: 0,
		TPSampleC: 0,
		TPSampleO: 0,
		TPAddValume: 0,
		TPAddMul: 0,
		TPAddType: 0,
		//总氮参数
		TNVolume: 0,
		TNSampleA: 0,
		TNSampleB: 0,
		TNSampleC: 0,
		TNSampleO: 0,
		TNAddValume: 0,
		TNAddMul: 0,
		TNAddType: 0,
		//COD参数
		CODVolume: 0,
		CODSampleA: 0,
		CODSampleB: 0,
		CODSampleC: 0,
		CODSampleO: 0,
		CODAddValume: 0,
		CODAddMul: 0,
		CODAddType: 0,
		//混合参数
		MIXVolume: 0,
		MIXSampleA: 0,
		MIXSampleB: 0,
		MIXSampleC: 0,
		MIXSampleO: 0,
		MIXAddValume: 0,
		MIXAddMul: 0,
		MIXAddType: 0,
		
		//系统服务
		mGpioOutD1: false,
		mGpioOutD2: false,
		mGpioOutD3: false,
		mGpioOutD4: false,
		mGpioOutD5: false,
		mGpioOutD6: false,
		mGpioOutD7: false,
		mGpioOutD8: false,
		mGpioOutP1: false,
		mGpioOutP2: false,
		mGpioOutP3: false,
		mGpioOutH1: false,
		mGpioOutLED: false,
		mGpioOut24V: false,
		mGpioOutDC1: false,
		mGpioOutRE1: false,
		mGpioOutDC2: false,
		mGpioOutRE2: false,
		statusC1: false,
		statusC2: false,
		statusC3: false,
		statusC4: false,
		statusC5: false,
		statusC6: false,
		mGpioIn1: false,
		mGpioIn2: false,
		mGpioIn3: false,
		mGpioIn4: false,
		mGpioIn5: false,
		mGpioIn6: false,
		statusS1: false,
		statusS2: false,
		statusS3: false,
		statusS4: false,
		statusS5: false,
		statusS6: false,
		statusS7: false,
		statusS8: false,
		statusS9: false,
		statusS10: false,
		statusS11: false,
		statusS12: false,
		isNotice: false,
		isSaveLog: false,
		errorId: 0,
		
	},
    created: function created () {
        this.canUseH5WebSocket()
        var address = localStorage.getItem('address');
        if (typeof address === 'string') this.address = address
        window.onerror = function (ev) {
            //console.warn(ev)
        }
    },
    filters: {
        rStatus: function (value) {
            switch (value) {
                case undefined:
                    return '尚未创建'
                case 0 :
                    return '尚未开启'
                case 1:
                    return '连接成功'
                case 2:
                    return '正在关闭'
                case 3:
                    return '连接关闭'
            }
        }
    },
    methods: {
        showTips: function showTips (className, content) {
            clearTimeout(this.alert.timer);
            this.alert.state   = false;
            this.alert.class   = className;
            this.alert.content = content;
            this.alert.state   = true;
            this.alert.timer   = setTimeout(function () {
                Vm.alert.state = false;
            }, 3000);
        },
        autoWsConnect: function () {
            try {
                if (this.connected === false){
                    localStorage.setItem('address', this.address)
                    var wsInstance = new WebSocket(this.address);
                    var _this      = Vm
                    wsInstance.onopen    = function (ev) {
                        console.warn(ev)
                        _this.connected = true
                        var service     = _this.instance.url.replace('ws://', '').replace('wss://', '');
                        service         = (service.substring(service.length - 1) === '/') ? service.substring(0, service.length - 1) : service;
                        _this.writeAlert('success', 'OPENED => ' + service);
                    }
                    wsInstance.onclose   = function (ev) {
                        console.warn(ev)
                        _this.autoSend = false;
                        clearInterval(_this.autoTimer);
                        _this.connected = false;
                        _this.writeAlert('danger', 'CLOSED => ' + _this.closeCode(ev.code));
                    }
                    wsInstance.onerror   = function (ev) {
                        console.warn(ev)
                        //_this.writeConsole('danger', '发生错误 请打开浏览器控制台查看')
                    }
                    wsInstance.onmessage = function (ev) {
                        console.warn(ev)
                        if (!_this.recvPause) {
                            var data = ev.data
                            if (_this.recvClean) _this.messageData = [];
                            //_this.writeNews(0, data);
							_this.content = data;
							_this.getJsonCodData(data);
                        }
                    }
                    this.instance        = wsInstance;
                }else {
                    this.instance.close(1000, 'Active closure of the user')
                }
            } catch (err) {
                console.warn(err)
                this.writeAlert('danger', '创建 WebSocket 对象失败 请检查服务器地址')
            }
        },
        autoHeartBeat: function () {
            var _this = Vm
            if (_this.autoSend === true) {
                _this.autoSend = false;
                clearInterval(_this.autoTimer);
            } else {
                _this.autoSend  = true
                _this.autoTimer = setInterval(function () {
                    //_this.writeConsole('info', '循环发送: ' + _this.heartBeatContent)
                    _this.sendData(_this.heartBeatContent)
                }, _this.heartBeatSecond * 1000);
            }
        },
        writeConsole: function (className, content) {
            this.consoleData.push({
                content: content,
                type: className,
                time: moment().format('HH:mm:ss')
            });
            this.$nextTick(function () {
                Vm.scrollOver(document.getElementById('console-box'));
            })
        },
        writeNews: function (direction, content, callback) {
            if (typeof callback === 'function') {
                content = callback(content);
            }

            this.messageData.push({
                direction: direction,
                content: content,
                time: moment().format('HH:mm:ss')
            });

            this.$nextTick(function () {
                if (!Vm.recvClean) {
                    Vm.scrollOver(document.getElementById('message-box'));
                }
            })
        },
        writeAlert: function (className, content) {
            //this.writeConsole(className, content);
            this.showTips(className, content);
        },
        canUseH5WebSocket: function () {
            if ('WebSocket' in window) {
                this.writeAlert('success', '初始化完成')
            }
            else {
                this.writeAlert('danger', '当前浏览器不支持 H5 WebSocket 请更换浏览器')
            }
        },
        closeCode: function (code) {
            var codes = {
                1000: '1000 CLOSE_NORMAL',
                1001: '1001 CLOSE_GOING_AWAY',
                1002: '1002 CLOSE_PROTOCOL_ERROR',
                1003: '1003 CLOSE_UNSUPPORTED',
                1004: '1004 CLOSE_RETAIN',
                1005: '1005 CLOSE_NO_STATUS',
                1006: '1006 CLOSE_ABNORMAL',
                1007: '1007 UNSUPPORTED_DATA',
                1008: '1008 POLICY_VIOLATION',
                1009: '1009 CLOSE_TOO_LARGE',
                1010: '1010 MISSING_EXTENSION',
                1011: '1011 INTERNAL_ERROR',
                1012: '1012 SERVICE_RESTART',
                1013: '1013 TRY_AGAIN_LATER',
                1014: '1014 CLOSE_RETAIN',
                1015: '1015 TLS_HANDSHAKE'
            }
            var error = codes[code];
            if (error === undefined) error = '0000 UNKNOWN_ERROR 未知错误';
            return error;
        },
        sendData: function (raw) {
            var _this = Vm
            var data  = raw
			_this.sendCmd = raw
            if (typeof data === 'object') {
                data = _this.content
            }
            try {
                _this.instance.send(data);
                //_this.writeNews(1, data);
                if (_this.sendClean && typeof raw === 'object') _this.content = '';
            } catch (err) {
                _this.writeAlert('danger', '消息发送失败 原因请查看控制台');
                throw err;
            }
        },
		getJsonCodData: function getJsonCodData(data){
			var obj = JSON.parse(data);
			var _this = Vm;
			
			//系统登录
			if(obj.respond === 'LOGIN_Ok'){
				_this.respond = obj.respond;
				_this.token = obj.token;
				_this.username = obj.user;
			}
			if(obj.respond === 'LOGIN_No'){
				_this.respond = obj.respond;
				_this.token = obj.token;
				_this.username = obj.user;
				_this.makeToast("验证错误或登录超时，请重新登录", "错误", 'danger', 'b-toaster-top-center');
				//_this.makeToast('success');
			}
			
			//首页运行状态
			if(obj.respond === 'RUN_Status'){
				_this.respond = obj.respond;
				_this.isRun = obj.isRun;
				_this.sysTime = obj.sysTime;
				_this.currentTime = obj.currentTime;
				_this.progressRate = obj.progressRate;
				_this.statusMsg = obj.statusMsg;
				_this.errorMsg = obj.errorMsg;
				_this.startTime = obj.startTime;
				_this.endTime = obj.endTime;
				_this.deviceList = obj.deviceList;
				_this.webServiceFlag = obj.webServiceFlag;
				_this.workType = obj.workType;
				_this.workFrom = obj.workFrom;
				_this.tempBox = obj.tempBox;
				_this.waterType = obj.waterType;
				_this.sampleType = obj.sampleType;
				_this.strSpecimen = obj.strSpecimen;
				_this.concentration = obj.concentration;
				_this.waterVolumeNow = obj.waterVolumeNow;
				_this.reagentVolumeNow = obj.reagentVolumeNow;
				_this.startSupplySamples = obj.startSupplySamples;
				_this.supplySamplesTime = obj.supplySamplesTime;
				_this.startSupplySamplesTime = obj.startSupplySamplesTime;
			}
			
			//数据查询
			if(obj.respond === 'GET_TaskData'){
				_this.respond = obj.respond;
				var taskdata = JSON.parse(obj.data);
				_this.itemsTask = taskdata;
			}
			if(obj.respond === 'GET_RecordData'){
				_this.respond = obj.respond;
				var recorddata = JSON.parse(obj.data);
				_this.itemsRecord = recorddata;
			}
			if(obj.respond === 'GET_AlertData'){
				_this.respond = obj.respond;
				var alertdata = JSON.parse(obj.data);
				_this.itemsAlert = alertdata;
			}
			
			//系统设置
			if(obj.respond === 'GET_Setup' || obj.respond === 'SET_Setup'){
				_this.respond = obj.respond;
				//定时任务
				_this.nextStartTime = obj.nextStartTime;
				_this.startCycle = obj.startCycle;
				_this.numberTimes = obj.numberTimes;
				_this.startWaterType = obj.startWaterType;
				_this.startSampleType = obj.startSampleType;
				//系统参数
				_this.deviceList = obj.deviceList;
				_this.BAUD_RATE = obj.BAUD_RATE;
				_this.MODBUS_ADDR = obj.MODBUS_ADDR;
				_this.version = obj.version;
				//基本设置
				_this.supplySamplesTime = obj.supplySamplesTime;
				_this.waterStepVolume = obj.waterStepVolume;
				_this.reagentStepVolume = obj.reagentStepVolume;
				_this.mixedTime = obj.mixedTime;
				//氨氮参数
				_this.NH3Volume = obj.NH3Volume;
				_this.NH3SampleA = obj.NH3SampleA;
				_this.NH3SampleB = obj.NH3SampleB;
				_this.NH3SampleC = obj.NH3SampleC;
				_this.NH3SampleO = obj.NH3SampleO;
				_this.NH3AddValume = obj.NH3AddValume;
				_this.NH3AddMul = obj.NH3AddMul;
				_this.NH3AddType = obj.NH3AddType;
				//总磷参数
				_this.TPVolume = obj.TPVolume;
				_this.TPSampleA = obj.TPSampleA;
				_this.TPSampleB = obj.TPSampleB;
				_this.TPSampleC = obj.TPSampleC;
				_this.TPSampleO = obj.TPSampleO;
				_this.TPAddValume = obj.TPAddValume;
				_this.TPAddMul = obj.TPAddMul;
				_this.TPAddType = obj.TPAddType;
				//总氮参数
				_this.TNVolume = obj.TNVolume;
				_this.TNSampleA = obj.TNSampleA;
				_this.TNSampleB = obj.TNSampleB;
				_this.TNSampleC = obj.TNSampleC;
				_this.TNSampleO = obj.TNSampleO;
				_this.TNAddValume = obj.TNAddValume;
				_this.TNAddMul = obj.TNAddMul;
				_this.TNAddType = obj.TNAddType;
				//COD参数
				_this.CODVolume = obj.CODVolume;
				_this.CODSampleA = obj.CODSampleA;
				_this.CODSampleB = obj.CODSampleB;
				_this.CODSampleC = obj.CODSampleC;
				_this.CODSampleO = obj.CODSampleO;
				_this.CODAddValume = obj.CODAddValume;
				_this.CODAddMul = obj.CODAddMul;
				_this.CODAddType = obj.CODAddType;
				//混合参数
				_this.MIXVolume = obj.MIXVolume;
				_this.MIXSampleA = obj.MIXSampleA;
				_this.MIXSampleB = obj.MIXSampleB;
				_this.MIXSampleC = obj.MIXSampleC;
				_this.MIXSampleO = obj.MIXSampleO;
				_this.MIXAddValume = obj.MIXAddValume;
				_this.MIXAddMul = obj.MIXAddMul;
				_this.MIXAddType = obj.MIXAddType;
				//提示信息
				if(obj.respond === 'GET_Setup'){
				_this.makeToast("参数已加载", "消息", 'success', 'b-toaster-top-right');
				}
				if(obj.respond === 'SET_Setup'){
				_this.makeToast("设置已成功", "消息", 'success', 'b-toaster-top-right');
				}
			}
						
			//系统服务
			if(obj.respond === 'GPIO_Status'){
				_this.respond = obj.respond;
				_this.mGpioOutD1 = obj.mGpioOutD1;
				_this.mGpioOutD2 = obj.mGpioOutD2;
				_this.mGpioOutD3 = obj.mGpioOutD3;
				_this.mGpioOutD4 = obj.mGpioOutD4;
				_this.mGpioOutD5 = obj.mGpioOutD5;
				_this.mGpioOutD6 = obj.mGpioOutD6;
				_this.mGpioOutD7 = obj.mGpioOutD7;
				_this.mGpioOutD8 = obj.mGpioOutD8;
				_this.mGpioOutP1 = obj.mGpioOutP1;
				_this.mGpioOutP2 = obj.mGpioOutP2;
				_this.mGpioOutP3 = obj.mGpioOutP3;
				_this.mGpioOutH1 = obj.mGpioOutH1;
				_this.mGpioOutLED = obj.mGpioOutLED;
				_this.mGpioOut24V = obj.mGpioOut24V;
				_this.mGpioOutDC1 = obj.mGpioOutDC1;
				_this.mGpioOutRE1 = obj.mGpioOutRE1;
				_this.mGpioOutDC2 = obj.mGpioOutDC2;
				_this.mGpioOutRE2 = obj.mGpioOutRE2;
				_this.statusC1 = obj.statusC1;
				_this.statusC2 = obj.statusC2;
				_this.statusC3 = obj.statusC3;
				_this.statusC4 = obj.statusC4;
				_this.statusC5 = obj.statusC5;
				_this.statusC6 = obj.statusC6;
				_this.mGpioIn1 = obj.mGpioIn1;
				_this.mGpioIn2 = obj.mGpioIn2;
				_this.mGpioIn3 = obj.mGpioIn3;
				_this.mGpioIn4 = obj.mGpioIn4;
				_this.mGpioIn5 = obj.mGpioIn5;
                _this.mGpioIn6 = obj.mGpioIn6;
				_this.statusS1 = obj.statusS1;
				_this.statusS2 = obj.statusS2;
				_this.statusS3 = obj.statusS3;
				_this.statusS4 = obj.statusS4;
				_this.statusS5 = obj.statusS5;
				_this.statusS6 = obj.statusS6;
				_this.statusS7 = obj.statusS7;
				_this.statusS8 = obj.statusS8;
				_this.statusS9 = obj.statusS9;
				_this.statusS10 = obj.statusS10;
				_this.statusS11 = obj.statusS11;
				_this.statusS12 = obj.statusS12;
				_this.isNotice = obj.isNotice;
				_this.isSaveLog = obj.isSaveLog;
				_this.errorId = obj.errorId;
			}
			if(obj.respond === 'CMD_Ok'){
				_this.respond = obj.respond;
				_this.makeToast("命令已执行", "消息", 'success', 'b-toaster-top-right');
			}
			if(obj.respond === 'CMD_No'){
				_this.respond = obj.respond;
				_this.makeToast("仪器正在分析，请稍后再试！", "警告", 'danger', 'b-toaster-top-right');
			}
			
			//控制台输出
			//console.log(data);
		},
        scrollOver: function scrollOver (e) {
            if (e) {
                e.scrollTop = e.scrollHeight;
            }
        },
        cleanMessage: function () {
            this.messageData = [];
        },
		makeToast(massage = null, title = "info", variant = 'default', toaster = 'b-toaster-top-right') {
			this.$bvToast.toast(massage, {
			  title: title,
			  variant: variant,
			  toaster: toaster,
			  solid: true
			})
		}
    },
	computed: {
		//数据查询
		rowsTask() {
			return this.itemsTask.length;
		},
		rowsRecord() {
			return this.itemsRecord.length;
		},
		rowsAlert() {
			return this.itemsAlert.length;
		},
		//计算剩余时间
		remainingTime(){
		    return Math.round(this.supplySamplesTime*60 - (this.currentTime - this.startSupplySamplesTime)/1000);
		},
		remainingProgress(){
		    return Math.round((this.remainingTime/(this.supplySamplesTime*60))*100);
		}


	}
})
