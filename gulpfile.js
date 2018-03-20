var gulp = require('gulp');
var mocha = require('gulp-mocha');
var uglify = require('gulp-uglify');
var rename = require('gulp-rename');

gulp.task('compress', function() {
    return gulp.src('src/main/resources/grooscript.js')
        .pipe(rename({suffix: '.min'}))
        .pipe(uglify())
        .pipe(gulp.dest('dist'))
});

gulp.task('tests', function () {
    return gulp.src('src/test/js/test.js', {read: false})
        .pipe(mocha({reporter: 'nyan'}));
});

gulp.task('test_require', function () {
    return gulp.src('src/test/js/require_test.js', {read: false})
        .pipe(mocha({reporter: 'nyan'}));
});

gulp.task('default', function() {
    gulp.start('compress');
});