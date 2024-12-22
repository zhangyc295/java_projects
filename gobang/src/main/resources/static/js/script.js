//  gameInfo 对象
var gameInfo = {
    roomId: null,
    playerId: null,
    opponentId: null,
    isBlack: true,
}

function setScreenText(me) {
    let screen = document.querySelector('#screen');
    if (me) {
        $("#screen").text("轮到你落子了!");
    } else {
        $("#screen").text("轮到对方落子了!");
    }
}

// 初始化 websocket 指定ip
//var websocket = new WebSocket("ws://127.0.0.1:8080/game");
//let websocketUrl = "ws://127.0.0.1:8080/game"
let websocketUrl = "ws://" + location.host + "/game";
var websocket = new WebSocket(websocketUrl);
// websocket对应方法
websocket.onopen = function () {
    console.log("连接建立!" + gameInfo.playerId);
}

websocket.onclose = function () {
    console.log("连接断开! " + gameInfo.playerId);
}

websocket.onerror = function () {
    console.log("连接异常! " + gameInfo.playerId);
}

window.onbeforeunload = function () {
    websocket.close();
}
websocket.onmessage = function (result) {
    // 处理服务器返回的响应
    console.log(result.data);
    let resp = JSON.parse(result.data);
    if (!resp.success) {
        alert("连接失败!" + resp.reason);
        location.href = "game_center.html";
        return;
    }
    if (resp.message == 'gameReady') {
        gameInfo.roomId = resp.roomId;
        gameInfo.playerId = resp.playerId;
        gameInfo.opponentId = resp.opponentId;
        gameInfo.isBlack = (resp.isBlack == resp.playerId)
        //gameInfo.isBlack = resp.isBlack;
    } else if (resp.message != 'repeatConnection') {
        console.log("游戏大厅连接失败！" + resp.reason);
        alert("游戏大厅连接失败！" + resp.reason);
        location.replace("login.html");
        return;
    }
    //游戏初始化
    initGame();
    setScreenText(gameInfo.isBlack);
}

//初始化
function initGame() {
    // 轮到我下还是对方下
    var me = gameInfo.isBlack;
    // 游戏是否结束
    var over = false;
    var chessBoard = [];
    //初始化chessBord数组(表示棋盘的数组)
    for (var i = 0; i < 15; i++) {
        chessBoard[i] = [];
        for (var j = 0; j < 15; j++) {
            chessBoard[i][j] = 0;
        }
    }
    var chess = document.querySelector('#chess');
    var context = chess.getContext('2d');
    context.strokeStyle = "#BFBFBF";
    // 背景图片
    var logo = new Image();
    logo.src = "pic/sky.jpg";
    logo.onload = function () {
        context.drawImage(logo, 0, 0, 450, 450);
        initChessBoard();
    }

    // 绘制棋盘网格
    function initChessBoard() {
        context.lineWidth = 1; // 设置线条粗细
        context.strokeStyle = "#8B8B8B";
        for (var i = 0; i < 15; i++) {
            context.moveTo(15 + i * 30, 15);
            context.lineTo(15 + i * 30, 435);
            context.stroke();
            context.moveTo(15, 15 + i * 30);
            context.lineTo(435, 15 + i * 30);
            context.stroke();
        }
    }

    // 绘制一个棋子
    function oneStep(i, j, isBlack) {
        context.beginPath();
        context.arc(15 + i * 30, 15 + j * 30, 13, 0, 2 * Math.PI);
        context.closePath();
        var gradient = context.createRadialGradient(15 + i * 30 + 2, 15 + j * 30 - 2, 13, 15 + i * 30 + 2, 15 + j * 30 - 2, 0);
        if (!isBlack) {
            gradient.addColorStop(0, "#D1D1D1");
            gradient.addColorStop(1, "#F9F9F9");
        } else {
            gradient.addColorStop(0, "#0A0A0A");
            gradient.addColorStop(1, "#636766");
        }
        context.fillStyle = gradient;
        context.fill();
    }

    chess.onclick = function (e) {
        if (over) {
            return;
        }
        if (!me) {
            return;
        }
        var x = e.offsetX;
        var y = e.offsetY;
        // 注意, 横坐标是列, 纵坐标是行
        var col = Math.floor(x / 30);
        var row = Math.floor(y / 30);
        if (chessBoard[row][col] == 0) {
            // 数据send给服务器   返回结果
            send(row, col);
        }
    }

    function send(row, col) {
        console.log("send: " + row + ", " + col);
        var request = {
            message: "putChess",
            playerId: gameInfo.playerId,
            row: row,
            col: col
        }
        websocket.send(JSON.stringify(request));
    }

    websocket.onmessage = function (body) {
        console.log(body.data);
        let resp = JSON.parse(body.data);
        if (resp.message != 'putChess') {
            console.log("响应错误！");
            return;
        }
        if (resp.playerId == gameInfo.playerId) {
            //自己落子
            oneStep(resp.col, resp.row, gameInfo.isBlack);
        } else if (resp.playerId == gameInfo.opponentId) {
            //对手落子
            oneStep(resp.col, resp.row, !gameInfo.isBlack);
        } else {
            console.log("落子响应错误！");
            console.log("gameInfo.opponentId" + gameInfo.opponentId);
            console.log("gameInfo.playerId" + gameInfo.playerId);
            return;
        }
        //掉线处理
        if (resp.row == null && resp.col == null) {
            if (resp.winner == gameInfo.playerId) {
                // 我赢了
                alert("您赢了！对方掉线！积分+10"); // alert 是 js 中弹对话框的函数.
            } else if (resp.winner == gameInfo.opponentId) {
                // 对方赢了
                alert("您输了！积分-5");
            }
            location.replace("game_center.html");
            return;
        }
        //设置落子
        chessBoard[resp.row][resp.col] = 1;
        me = !me;
        setScreenText(me);
        if (resp.winner != 0) {
            // 胜负已分
            if (resp.winner == gameInfo.playerId) {
                // 自己胜 对手负
                alert("您赢了！积分+10"); // alert 是 js 中弹对话框的函数.
            } else if (resp.winner == gameInfo.opponentId) {
                // 对手胜 自己负
                alert("您输了！积分-5");
            } else {
                alert("系统错误！");
            }
            setTimeout(function () {
                location.replace("game_center.html");
            }, 3000); // 3000 毫秒 = 3 秒
            //location.href = "game_center.html";
            //location.assign('/game_center.html');
            // 页面刷新
            //window.location.reload();
        }
    }
}

