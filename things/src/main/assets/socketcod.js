"use strict"
var Vm = new Vue({
    el: '#root',
    data: {
        consoleData: [], // 控制台日志
        messageData: [], // 消息记录
        instance: WebSocket, // ws instance
        address: 'ws://10.10.0.139:8081',
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
		respond: '',
		isRun: false,
		sysTime: '',
		codValue: 0,
		progressRate: 0,
		statusMsg: '',
		startTime: '',
		endTime: '',
		tempIn: 0,
		tempOut: 0,
		adLight: 0,
		errorMsg: '',
		startXiaojie: '',
		endXiaoJie: '',
		didingNum: 0,
		didingSumVolume: 0,
		deviceList: '',
		webServiceFlag: true,
		workType: '',
		workFrom: '',
		sendCmd: '',
		tempBox: 0,
		loading: '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>',
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
		mGpioIn1: false,
		mGpioIn2: false,
		mGpioIn3: false,
		mGpioIn4: false,
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
		isEmptyPipeline: false,
		isNotice: false,
		nextStartTime: 0,
		startCycle: 0,
		numberTimes: 0,
		isLoop: false,
		xiaojieTemp: 0,
		xiaojieTime: 0,
		biaodingValue: 0,
		deviceList: '',
		BAUD_RATE: 9600,
		MODBUS_ADDR: 3,
		errorId: 0,
		date: '',
		time: '',
		connecting: '<div class="float-right spinner-grow text-success spinner-grow-sm" role="status"><span class="sr-only">Loading...</span></div>',
		perPageCod: 10,
		currentPageCod: 1,
		itemsCod: 0,
		perPageAlert: 10,
		currentPageAlert: 1,
		itemsAlert: 0,
		perPageCalibration: 10,
		currentPageCalibration: 1,
		itemsCalibration: 0,
		chartData: 0,
		chartTime: 0,
		username: '',
		password: '',
		token: ''
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
			if(obj.respond === 'RUN_Status'){
				_this.respond = obj.respond;
				_this.isRun = obj.isRun;
				_this.sysTime = obj.sysTime;
				_this.codValue = obj.codValue;
				_this.progressRate = obj.progressRate;
				_this.statusMsg = obj.statusMsg;
				_this.startTime = obj.startTime;
				_this.endTime = obj.endTime;
				_this.tempIn = obj.tempIn;
				_this.tempOut = obj.tempOut;
				_this.adLight = obj.adLight;
				_this.errorMsg = obj.errorMsg;
				_this.startXiaojie = obj.startXiaojie;
				_this.endXiaoJie = obj.endXiaoJie;
				_this.didingNum = obj.didingNum;
				_this.didingSumVolume = obj.didingSumVolume;
				_this.deviceList = obj.deviceList;
				_this.webServiceFlag = obj.webServiceFlag;
				_this.workType = obj.workType;
				_this.workFrom = obj.workFrom;
				_this.tempBox = obj.tempBox;
			}
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
				_this.mGpioIn1 = obj.mGpioIn1;
				_this.mGpioIn2 = obj.mGpioIn2;
				_this.mGpioIn3 = obj.mGpioIn3;
				_this.mGpioIn4 = obj.mGpioIn4;
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
				_this.isEmptyPipeline = obj.isEmptyPipeline;
				_this.isNotice = obj.isNotice;
				_this.errorId = obj.errorId;
			}
			if(obj.respond === 'CMD_Ok'){
				_this.respond = obj.respond;
				_this.makeToast("命令已执行", "消息", 'success', 'b-toaster-top-right');
			}
			if(obj.respond === 'CMD_No'){
				_this.respond = obj.respond;
				_this.makeToast("命令未执行，可能仪器正在运行", "出错", 'danger', 'b-toaster-top-right');
			}
			if(obj.respond === 'GET_Setup'){
				_this.respond = obj.respond;
				_this.nextStartTime = obj.nextStartTime;
				_this.startCycle = obj.startCycle;
				_this.numberTimes = obj.numberTimes;
				_this.isLoop = obj.isLoop;
				_this.xiaojieTemp = obj.xiaojieTemp;
				_this.xiaojieTime = obj.xiaojieTime;
				_this.biaodingValue = obj.biaodingValue;
				_this.deviceList = obj.deviceList;
				_this.BAUD_RATE = obj.BAUD_RATE;
				_this.MODBUS_ADDR = obj.MODBUS_ADDR;
			}
			if(obj.respond === 'SET_Setup'){
				_this.respond = obj.respond;
				_this.nextStartTime = obj.nextStartTime;
				_this.startCycle = obj.startCycle;
				_this.numberTimes = obj.numberTimes;
				_this.isLoop = obj.isLoop;
				_this.xiaojieTemp = obj.xiaojieTemp;
				_this.xiaojieTime = obj.xiaojieTime;
				_this.biaodingValue = obj.biaodingValue;
				_this.deviceList = obj.deviceList;
				_this.BAUD_RATE = obj.BAUD_RATE;
				_this.MODBUS_ADDR = obj.MODBUS_ADDR;
				_this.makeToast("设置已成功", "消息", 'success', 'b-toaster-top-right');
			}
			if(obj.respond === 'GET_CodData'){
				_this.respond = obj.respond;
				var coddata = JSON.parse(obj.data);
				_this.itemsCod = coddata;
			}
			if(obj.respond === 'GET_AlertData'){
				_this.respond = obj.respond;
				var alertdata = JSON.parse(obj.data);
				_this.itemsAlert = alertdata;
			}
			if(obj.respond === 'GET_CalibrationData'){
				_this.respond = obj.respond;
				var calibrationdata = JSON.parse(obj.data);
				_this.itemsCalibration = calibrationdata;
			}
			if(obj.respond === 'GET_NewData'){
				_this.respond = obj.respond;
				_this.chartData = JSON.parse(obj.codData);
				_this.chartTime = JSON.parse(obj.codTime);
				options.series[0].data = _this.chartData;
				options.xaxis.categories = _this.chartTime;
			}
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
		rowsCod() {
			return this.itemsCod.length
		},
		rowsAlert() {
			return this.itemsAlert.length
		},
		rowsCalibration() {
			return this.itemsCalibration.length
		}
	}
})
