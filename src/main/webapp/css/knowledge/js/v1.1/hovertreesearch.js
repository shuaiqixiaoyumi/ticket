function searchToggle(obj, evt) {
    var container = $(obj).closest('.search-wrapper');

    if (!container.hasClass('active')) {
        container.addClass('active');
        evt.preventDefault();
    }
    else if (container.hasClass('active') && $(obj).closest('.input-holder').length == 0) {
        container.removeClass('active');
        // clear input
        container.find('.search-input').val('');
        // clear and hide result container when we press close
        container.find('.result-container').fadeOut(100, function () { $(this).empty(); });
    }
}

function submitFn(obj, evt) {
    var value = $(obj).find('.search-input').val().trim();

    var _html = "搜索词： ";
    if (!value.length) {
        _html = "请输入关键词...";
    }
    else {
        /*拼接搜索链接
         * http://localhost:8080/dd-blog/KeySearch?key=a
         * */
        window.open("http://localhost:8080/dd-blog/KeySearch?key=" + value,"_self");
        _html += "<b>" + value + "</b>";
    }

    $(obj).find('.result-container').html('<span>' + _html + '</span>');
    $(obj).find('.result-container').fadeIn(100);

    evt.preventDefault();
}