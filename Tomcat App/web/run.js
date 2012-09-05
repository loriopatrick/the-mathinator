var WB = {} || new Object();
WB.store = {
    mode:0,
    target:'x'
};
WB.size = {
    isBig:true,
    small:function (callback) {
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
    big:function (callback) {
//        $('#eq').focus();
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
    up:function () {
        new Spinner({top:20, className:'spinner'}).spin(document.body);
    },
    down:function () {
        $('.spinner').remove();
    }
};

WB.engine = {
    run:function () {
        if ($('#eq').val().length == 0) $('#eq').val($('#eq').attr('placeholder'));
        if ($('#target').val().length == 0) $('#target').val($('#target').attr('placeholder'));
        var eq = this.getEq();
        if (eq.length == 0) return;

        if (eq.indexOf(WB.store.target) == -1) {
            var vars = 'abcdefghijklmnopqrstuvwxyz';
            for (var i = 0; i < vars.length; ++i) {
                if (eq.indexOf(vars[i]) > -1) {
                    WB.store.target = vars[i];
                    $('#target').val(vars[i]);
                    break;
                }
            }
        }

        window.location.hash = '#' + eq + '!' + WB.store.target;
        WB.spinner.up();

        var _this = this;
        this.calc(eq, function () {
            _this.render();
            WB.spinner.down();
            WB.size.small();
        });
    },
    getEq:function () {
        var eq = $('#eq').val();
        WB.store.target = $('#target').val();
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
    calc:function (eq, callback) {
        $.post('calc/', {
            md: WB.store.mode,
            tg: WB.store.target,
            eq: eq
        }, function (raw) {
            var data = raw
                .split('\\sqrt').join('√')
                .split('\\pi').join('π')
                .split('\\partial').join('∂')
                .split('\n');
            console.log(data);
            $('#preview').html('We Read: $$' + data[0] + '$$');
            var res = [];
            var last = '';
            for (var i = 0; i < data.length; i++) {
                if (last == data[i]) continue;
                else last = data[i];

                res.push('<span style=\"padding: 7px;\">$$' + data[i] + '$$</span>');
            }
            $('#res').html(res.join('<br/>'));
            if (callback) callback();
        });
    },
    render:function () {
        M.parseMath($('#preview')[0]);
        M.parseMath($('#res')[0]);
    },
    parser:function (eq) {
        function replace(s, o, n) {
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
            return '+-()*^/=.'.indexOf(c) > -1;
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
//    $('#eq').focus();
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
        var parts = decodeURI(h.substr(1)).split('!');
        $('#eq').val(parts[0]);
        if (parts.length > 1)
            $('#target').val(parts[1]);
        setTimeout(function () {
            WB.engine.run();
        }, 700);
    }

    setInterval(function () {
        if (window.location.hash.length <= 1) {
            WB.size.big();
        }
    }, 100);


    $('#menu').change(function () {
        var i = this.selectedIndex;
        if (WB.store.mode == i) return;
        if (i == 0) {
            $('#eq').attr('placeholder', '4*(9*x+18*x/5)=32*x+6');
        } else if (i == 1) {
            $('#eq').attr('placeholder', '2*(x-3)+4*b-2*(x-b-3)+5');
        } else if (i == 2) {
            $('#eq').attr('placeholder', 'x^2+2x+1');
        }

        if (WB.store.mode == 1) {
            $('#target-holder').animate({
                width: 100
            });
        } else if (i == 1) {
            $('#target-holder').animate({
                width: 0
            });
        }

        WB.store.mode = i;
    });

    var last = 0;

    $('#eq').keydown(function () {
        if (WB.store.mode != 0 && $(this).val().indexOf('=') > -1) {
            $('#menu')[0].selectedIndex = 0;
            $('#target-holder').animate({
                width: 100
            });
            WB.store.mode = 0;
        }
    });
});