var node = {
    val:"*",
    nodes:[
        {
            val:"+",
            nodes:[
                { val:"1" },
                { val:"2" },
                { val:"3" }
            ]
        },
        {
            val:"/",
            nodes:[
                {
                    val:"+",
                    nodes:[
                        { val:"1" },
                        { val:"2" },
                        { val:"3" }
                    ]
                },
                {
                    val:"^",
                    nodes:[
                        { val:"1" },
                        {
                            val:"+",
                            nodes:[
                                { val:"1" },
                                { val:"2" }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
};

function display(elem, node) {
    if (!node.nodes || node.nodes.length == 0) {
        elem.innerHTML = node.val;
        return;
    }

    elem.style.padding = '0';

    var n = null;

    if (node.val == '+' || node.val == '*') {
        for (var i = 0; i < node.nodes.length; i++) {
            n = document.createElement('div');
            n.className = 'node';

            elem.appendChild(n);
            display(n, node.nodes[i]);

            if (i < node.nodes.length - 1) {
                n = document.createElement('div');
                n.className = 'node op';
                n.innerHTML = node.val == '*' ? '&#215;' : node.val;

                elem.appendChild(n);
            }
        }
    } else if (node.val == '/') {
        var d = document.createElement('div');
        d.className = 'node div';

        elem.appendChild(d);

        n = document.createElement('div');
        n.className = 'node nom';
        d.appendChild(n);
        display(n, node.nodes[0]);

        d.innerHTML += '<br/>';

        n = document.createElement('div');
        n.className = 'node den';
        d.appendChild(n);
        display(n, node.nodes[1]);
    } else if (node.val == '^') {
        var p = document.createElement('div');
        p.className = 'node pow';

        elem.appendChild(p);

        n = document.createElement('div');
        n.className = 'node power';

        display(n, node.nodes[1]);

        var e = document.createElement('div');
        e.className = 'node base';
        display(e, node.nodes[0]);


        p.appendChild(n);
//        p.appendChild(document.createElement('br'));
        p.appendChild(e);



        $(n).css({
            marginLeft: ($(e).width() + 5) + 'px'
        });
        console.log(n);
    }
}

function center () {
    var nodes = document.getElementsByClassName('node');
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        var parent = node.parentNode;

        $(parent).height($(parent).height());
        $(parent).width($(parent).width());

        $(node).height($(node).height());
        $(node).width($(node).width());

        if (parent.className.indexOf('node') == -1) continue;

        if (node.className.indexOf('nom') > -1 || node.className.indexOf('den') > -1) {
            $(node).css({
                marginLeft: ($(parent).width()/2 - $(node).width()/2) + 'px'
            });
            continue;
        }

        if (node.className.indexOf('power') > -1) continue;
        if (node.className.indexOf('base') > -1) continue;


        var ph = $(parent).height(),
            nh = $(node).height();

        if (ph == nh) continue;

        $(node).css({
            marginTop: (ph/2 - nh/2) + 'px'
        });
    }
}

window.onload = function () {
    var e = document.createElement('div');
    e.className = 'node';
    document.body.appendChild(e);
    display(e, node);
    center();
};