
var WB = {} || new Object();

WB.size = {
    isBig: true,
    small: function (callback) {
        this.isBig = false;
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
    },
    big: function (callback) {
        $('#eq').focus();
        if (this.isBig) return;
        this.isBig = true;
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
};

WB.spinner = {
    up: function () {
        new Spinner({top:20, className:'spinner'}).spin(document.body);
    },
    down: function () {
        $('.spinner').remove();
    }
};

WB.engine = {
    run: function () {
        var eq = this.getEq();
        WB.spinner.up();
        
        var _this = this;
        this.calc(eq, function () {
            _this.render();
            WB.spinner.down();
            WB.size.small();
        });
    },
    getEq: function () {
        var eq = $('#eq').val();
            $('#raw').html(eq);
            eq = this.parser(eq);
            var html = [
                '<div class="fb-send" data-href="http://themathinator.com/#',
                encodeURI(eq),
                '"></div>'
            ];
            $('#fb_share').html(html.join(''));
            FB.XFBML.parse($('#fb_share')[0]);
            
            return eq;
    },
    calc: function (eq, callback) {
        $.post('/calc/', eq, function (raw) {
            var data = raw.split('\n');
            $('#preview').html('We Read: <div class="math">' + data[0] + '</div>');
            var res = [];
            var last = '';
            for (var i = 0; i < data.length; i++) {
                if (last == data[i]) continue;
                else last = data[i];
                
                res.push('<div class=\"math\">' + data[i] + '</div>');
            }
            $('#res').html(res.join('<br/>'));  
            if (callback) callback();
        }, 'text');
    },
    render: function () {
        jsMath.ProcessBeforeShowing('preview');
        jsMath.ProcessBeforeShowing('res');
    },
    parser: function (eq) {
        function replace (s, o, n) {
            return s.split(o).join(n);
        }
        
        eq = replace(eq, String.fromCharCode(8722), '-');
        eq = replace(eq, ' ', '');
        eq = replace(eq, '[', '(');
        eq = replace(eq, ']', ')');
        eq = replace(eq, '-(', '-1*(');
        eq = replace(eq, '-x', '-1*x');
        
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
    
            if ((lastN && !num && !op) || (!lastN && !lastO && !op && num)) {
                res.push('*');
            }
    
            res.push(eq[i]);
            lastN = num;
            lastO = op;
        }
    
        return res.join('');
    }
};

$(document).ready(function () {
    $('#eq').focus();
    $(window).keydown(function (e) {
        if (e.keyCode == 13) {
            if (WB.size.isBig) {
                WB.engine.run();
            } else {
                WB.size.big();
            }
        }
    });

    var h = window.location.hash;
    if (h && h != '#') {
        $('#eq').val(decodeURI(h.substr(1)));
        setTimeout(function () {
            WB.engine.run();
        }, 700);
    }
});