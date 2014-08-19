var gulp = require('gulp');
var uglify = require('gulp-uglify');
var rename = require('gulp-rename');
var mocha = require('gulp-mocha');

gulp.task('compress', function() {
    return gulp.src('src/main/resources/META-INF/resources/grooscript.js')
        .pipe(rename({suffix: '.min'}))
        .pipe(uglify())
        .pipe(gulp.dest('src/main/resources/META-INF/resources'))
});

gulp.task('tests', function () {
    return gulp.src('src/test/js/test.js', {read: false})
        .pipe(mocha({reporter: 'nyan'}));
});

gulp.task('default', function() {
    gulp.start('compress');
});