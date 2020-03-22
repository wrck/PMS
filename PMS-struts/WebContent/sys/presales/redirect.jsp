<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<body>
    <form id="mainForm" name="mainForm" method="post" cssClass="form-inline" action="module/presales_list.action">
    </form>
    
<script>
    var url = '${redirect}';
    url = url.replace(new RegExp("&amp;", 'gm'), "&");//IE跳转正常、其他浏览器会...
    url = url.replace(new RegExp("＆", 'gm'), "&");
    url = '${pageContext.request.contextPath}/' + url;
    try {
        zyEs6AssignPolyfill();
        var redirect = url;
        var params = {};
        var regex = /(\?[^#]*)(#.*)?$/;
        var search = redirect.match(regex);
        var localSeach = "";
        if (search) {
            search = search[1];
        }
        var param1 = searchParse(search);
        localSearch = decodeURIComponent(localStorage.getItem("presalesRedirectSearch") || "");
        //localStorage.removeItem("presalesRedirectSearch");
        var param2 = searchParse(localSearch);
        Object.assign(params, param2, param1);
        var mainForm = document.getElementById("mainForm");
        if (!mainForm) {
            mainForm = document.createElement("form");
            document.body.appendChild(mainForm);
        }
        mainForm.method = "post";
        mainForm.action = redirect.replace(regex, "");
        var hiddenFormData = "";
        for (var key in params) {
            hiddenFormData += "<input type='hidden' name='" + key + "' value='" + params[key] + "' >";
        }
        if (hiddenFormData) {
            mainForm.innerHTML = hiddenFormData;
            mainForm.submit();
        } else {
            top.window.location = url;
        }
    } catch (e) {
        console.log(e);
        top.window.location = url;
    }

    function searchParse(search) {
        var resultObj = {};
        if (search && search.length > 1) {
            var search = search.substring(1);
            var items = search.split('&');
            for (var index = 0; index < items.length; index++) {
                if (!items[index]) {
                    continue;
                }
                var kv = items[index].split('=');
                resultObj[kv[0]] = typeof kv[1] === "undefined" ? "" : kv[1];
            }
        }
        return resultObj;
    }

    function zyEs6AssignPolyfill() {
        if (!Object.assign) {
            Object.defineProperty(Object, "assign", {
                enumerable : false,
                configurable : true,
                writable : true,
                value : function(target, firstSource) {
                    "use strict";
                    if (target === undefined || target === null)
                        throw new TypeError(
                                "Cannot convert first argument to object");
                    var to = Object(target);
                    for (var i = 1; i < arguments.length; i++) {
                        var nextSource = arguments[i];
                        if (nextSource === undefined
                                || nextSource === null)
                            continue;
                        var keysArray = Object
                                .keys(Object(nextSource));
                        for (var nextIndex = 0, len = keysArray.length; nextIndex < len; nextIndex++) {
                            var nextKey = keysArray[nextIndex];
                            var desc = Object
                                    .getOwnPropertyDescriptor(
                                            nextSource, nextKey);
                            if (desc !== undefined
                                    && desc.enumerable)
                                to[nextKey] = nextSource[nextKey];
                        }
                    }
                    return to;
                }
            });
        }
    }
</script>
</body>
</html>