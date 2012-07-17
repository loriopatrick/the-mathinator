var isBig = true;
function small(callback) {
    isBig = false;
    $('#head').animate({
        paddingTop:10
    }, 300, function () {
        $('#head').height($('#head').height());
        $('#head').animate({
            height:35
        }, 500);
        $('.title').animate({
            fontSize:24,
            width:300
        }, 300, callback)
    });
}
function big(callback) {
    $('#eq').focus();
    if (isBig) return;
    isBig = true;
    $('#head').animate({
        paddingTop:120
    }, 300, function () {
        $('#head').animate({
            height:$(window).height()
        }, 500);
        $('.title').animate({
            fontSize:48,
            width:550
        }, 300, function () {
            $('#head').css({
                bottom:0
            });
            if (callback) callback();
        })
    });
}
function render() {
    var elems = document.getElementsByClassName('eqSty');
    for (var i = 0; i < elems.length; i++) {
        MathJax.Hub.Queue(["Typeset", MathJax.Hub, elems[i]]);
    }
}
function query(eq, callback) {
    $.post('/calc/', eq, function (data) {
        callback(data.split('\n'));
    }, 'text');
}
function check(eq) {
    eq = eq.split(' ').join('');

    function isNum(input) {
        return (input - 0) == input && input.length > 0;
    }

    var res = [];
    var lastN = false;
    for (var i = 0; i < eq.length; i++) {
        var num = isNum(eq[i]);
        if ((eq[i] == '(' && lastN)
            || (i > 0 && eq[i - 1] == ')' && num)
            || (eq[i - 1] == ')' && eq[i] == '(')) {
            res.push('*');
        }

        res.push(eq[i]);
        lastN = num;
    }

    return res.join('');
}
var last = '';
function run() {
    var eq = $('#eq').val();

    log(eq);

    eq = check(eq);

    if (!eq) {
        alert('no input');
        return;
    }

    if (eq == last) {
        small();
        return;
    }

    last = eq;

    var spin = new Spinner({top:20, className:'spinner'}).spin(document.body);

    query(eq, function (data) {
        $('#raw').html(eq);
        $('#preview').html('\\[' + data[0] + '\\]');

        var res = [];
        for (var i = 0; i < data.length; i++) {
            res.push('\\[' + data[i] + '\\]');
        }

        $('#res').html(res.join('<br/>'));

        render();
        small();
        $('.spinner').remove();
    });
}
$(document).ready(function () {
    $('#eq').focus();
    $(window).keydown(function (e) {
        if (e.keyCode == 13) {
            if (isBig) {
                run();
            } else {
                big();
            }
        }
    });
});