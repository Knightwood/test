
           (function getSuggest(str) {
                var sugurl = "http://suggestion.baidu.com/su?wd=#content#&cb=window.baidu.sug";
                sugurl = sugurl.replace("#content#", str);
                window.baidu = {
                    sug: function(json) {
                    var x=[];
                    for (i = 0; i < json.s.length; i++) {
                        var str=json.s[i];
                        x.push(str);
                    };
                       SuggestJS.giveSuggest(x);
                    }
                };
                var script = document.createElement("script");
                script.src = sugurl;
                document.body.appendChild(script);
            })()