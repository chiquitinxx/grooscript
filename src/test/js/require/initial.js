define(['require/A', 'require/B'], function (A, B) {
    if (A() !== 1 || B().data !== 2 || B().a !== 1) {
        throw 'FAIL';
    }

    return 'OK';
});