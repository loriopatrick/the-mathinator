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
function type(s) {
    if (s[0] == '-') return type(s[1]);
    var c = s[0];
    if (c == '0'
        || c == '1'
        || c == '2'
        || c == '3'
        || c == '4'
        || c == '5'
        || c == '6'
        || c == '7'
        || c == '8'
        || c == '9') return 1;
    if (c == 'x') return 2;
    return 3;
}
function check(eq) {
    eq = eq.split(' ').join('');
    eq = eq.split('-x').join('-1*x');
    eq = eq.split('[').join('(');
    eq = eq.split(']').join(')');

    function isNum(input) {
        return (input - 0) == input && input.length > 0;
    }

    function isOp(c) {
        return '+-()*^/'.indexOf(c) > -1;
    }

    var res = [];
    var lastN = false;
    var lastO = true;

    for (var i = 0; i < eq.length; i++) {
        var num = isNum(eq[i]);
        var op = isOp(eq[i]);

        if ((eq[i] == '(' && lastN)
            || (i > 0 && eq[i - 1] == ')' && num)
            || (eq[i - 1] == ')' && eq[i] == '(')) {
            res.push('*');
        }

        if ((lastN && !num && !op) || !lastN && !lastO && !op) {
            res.push('*');
        }

        res.push(eq[i]);
        lastN = num;
        lastO = op;
    }

    return res.join('');
}
function shareButton(eq) {
    var html = [
        '<div class="fb-send" data-href="http://themathinator.com/#',
        encodeURI(eq),
        '"></div>'
    ];
    $('#fb_share').html(html.join(''));
    FB.XFBML.parse($('#fb_share')[0]);
}
var last = '';
function run() {
    var eq = $('#eq').val();
    $('#raw').html(eq);
    shareButton(eq);

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
        $('#preview').html('\\[' + data[0] + '\\]');

        var res = [];
        var last = '';
        for (var i = 0; i < data.length; i++) {
            if (last == data[i]) continue;
            last = data[i];
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

    var h = window.location.hash;
    if (h && h != '#') {
        $('#eq').val(decodeURI(h.substr(1)));
        setTimeout(function () {
            run();
        }, 700);
    }

});