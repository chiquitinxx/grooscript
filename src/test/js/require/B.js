define(['require/A', 'require/A'], function (A, AA) {
    function B() {
        var me = {};
        me.a = A();
        me.aPlus = AA();
        me.data = 2;
        return me;
    }

    return B;
});