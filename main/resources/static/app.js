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
            showGreeting(JSON.parse(greeting.body).responseCode);
            showGreeting(JSON.parse(greeting.body).message);
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
    return (10 - (($("#orderQty").val() + $("#price").val()) % 10))
}

function generateClOrdID () {
    $("#clOrdID").val(Math.floor(Math.random()*100000));
}

function togglePriceDisplay() {
       var priceDisplay = document.getElementById("priceDisplay")
      if ($("#ordType").is(":checked")) {
        priceDisplay.style.display = "none";
        $("#price").val("");
      } else {
        priceDisplay.style.display = "block";
      }
}

function sendForm() {
    stompClient.send("/app/transact", {}, JSON.stringify({
        'ordType': $("#ordType").is(":checked"),
        'side'   : $("#side").is(":checked"),
        'symbol' : $("#symbol").val(),
        'orderQty' : parseInt($("#orderQty").val()),
        'price'  : parseFloat($("#price").val()),
        'clOrdID'  : $("#clOrdID").val().toString(),
        'checksum' : calculateChecksum()
    }));
    generateClOrdID();
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#ordType" ).on('change', function() { togglePriceDisplay(); });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendForm(); });
});

