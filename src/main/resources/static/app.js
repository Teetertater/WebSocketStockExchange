var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    generateClOrdID();
    var socket = new SockJS('/endpoint');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showOrders(JSON.parse(greeting.body));
        });
        stompClient.subscribe('/user/queue/acks', function (ack) {
            showAck(JSON.parse(ack.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function calculateChecksum() {
    var sum = parseInt($("#orderQty").val()) + parseFloat($("#price").val())
    return (10 - (sum) % 10)
}

function generateClOrdID () {
    $("#clOrdID").val(Math.floor(Math.random()*100000));
}

function sendForm() {
    stompClient.send("/app/transact", {}, JSON.stringify({
        'ordType': "LIMIT",
        'side'   : $("#side").is(":checked"),
        'symbol' : $("#symbol").val(),
        'orderQty' : parseInt($("#orderQty").val()),
        'price'  : parseFloat($("#price").val()),
        'clOrdID'  : $("#clOrdID").val().toString(),
        'checksum' : calculateChecksum()
    }));
    generateClOrdID();
}

function showAck(message) {
   $("#acks").append("<tr><td>" + message.responseCode + "</td><td>" + message.message + "</td></tr>");
}

function showOrders(message) {
    $("#orderBookBuy").dataTable().fnClearTable();
    if (message.buyTopN.length !== 0) {
        $("#orderBookBuy").dataTable().fnAddData(message.buyTopN);
    }
    $("#orderBookSell").dataTable().fnClearTable();
    if (message.sellTopN.length !== 0) {
        $("#orderBookSell").dataTable().fnAddData(message.sellTopN);
    }
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#ordType" ).on('change', function() { togglePriceDisplay(); });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendForm(); });
    $( "#orderBookBuy").DataTable({
            "processing": true,
            "searching": false,
            "paging": false,
            "ordering": false,
            "info" : false,
            "columns": [
                   { "data": "timestamp" },
                   { "data": "ordType" },
                   { "data": "side" },
                   { "data": "symbol" },
                   { "data": "orderQty" },
                   { "data": "price" },
                   { "data": "clOrdID" }
               ]
           } );
    $( "#orderBookSell").DataTable({
                "processing": true,
                "searching": false,
                "paging": false,
                "ordering": false,
                "info" : false,
                "columns": [
                       { "data": "timestamp" },
                       { "data": "ordType" },
                       { "data": "side" },
                       { "data": "symbol" },
                       { "data": "orderQty" },
                       { "data": "price" },
                       { "data": "clOrdID" }
                   ]
               } );
});

