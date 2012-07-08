
function calc (eq) {
    $.post('http://127.0.0.1:8080/calc/', eq, function (data) {
        var ar = data.split('\n');
        var res = [];
        for (var i = 0; i < ar.length; i++) {
            res.push('\\[' + ar[i] + '\\]');
        }
        $('#res').html(res.join('<br/>'));
        MathJax.Hub.Queue(["Typeset",MathJax.Hub,"res"]);
    }, 'text');
}