!
    function(o) {
        $(window.QunHandler.member);
        var e = function() {},
            i = 0,
            a = "/cgi-bin/qun_mgr/",
            r = "get_group_list",
            n = "get_friend_list",
            s = "add_group_member",
            c = "set_group_admin";
        deleteMember = "delete_group_member",
            setMemberCard = "set_group_card",
            setGroupTags = "set_group_mem_tag",
            addMemberTag = "add_member_tag",
            delMemberTag = "del_member_tag",
            setMemberTag = "set_member_tag",
            u = "search_group_members",
            faceurl = "//face.imweb.qq.com/cgi-bin/face?app=group_info";
        var l = function(e, t) {
            var r = window.performance;
            if (report_core.monitor(2388543), r && r.getEntriesByName) {
                var a = r.getEntriesByName(e),
                    n = a[a.length - 1];
                if (n) {
                    var o = {
                        1 : n.redirectEnd - n.redirectStart,
                        2 : n.domainLookupStart - n.fetchStart,
                        3 : n.domainLookupEnd - n.domainLookupStart,
                        4 : n.connectEnd - n.connectStart,
                        5 : n.responseStart - n.requestStart,
                        6 : n.responseEnd - n.responseStart,
                        7 : n.responseEnd - n.startTime,
                        8 : n.fetchStart,
                        9 : n.domainLookupStart
                    };
                    5e3 < o[2] && (report_core.monitor(2388544), BJ_REPORT.info(JSON.stringify({
                        "重定向": o[1],
                        appcache: o[2],
                        dns: o[3],
                        tcp: o[4],
                        "接收": o[5],
                        "完成": o[6],
                        "总时间": o[7],
                        fetchStart: n.fetchStart,
                        dnsstart: n.domainLookupStart,
                        header: t.getAllResponseHeaders && t.getAllResponseHeaders() || !1,
                        status: t.status,
                        t: "qunweb0705",
                        url: e
                    })))
                }
            }
        };
        function d(a, n, o) {
            "function" != typeof a && (a = e);
            var i = +new Date;
            return function(e, t, r) {
                try {
                    0 === n.indexOf("/cgi") && (n = "//qun.qq.com" + n),
                        reportCgi.report({
                            url: n,
                            type: 1,
                            code: e.ec,
                            time: +new Date - i,
                            rete: 10,
                            uin: $.getQQ()
                        })
                } catch(e) {}
                0 != e.ec && o && report_core.monitor(o),
                    l(n, r),
                    a(e, t, r)
            }
        }
        function u(e, t) {
            t = d(t)
        }
        o.member.model = {
            getGroup: function(e) {
                e = d(e, a + r, 397734);
                var t = {
                    type: "POST",
                    url: a + r,
                    dataType: "json",
                    error: e,
                    success: e
                };
                o.request(t)
            },
            getFriend: function(e) {
                e = d(e, a + n, 397733);
                var t = {
                    type: "POST",
                    url: a + n,
                    dataType: "json",
                    error: e,
                    success: e
                };
                o.request(t)
            },
            getMember: function(e, t) {
                if ("object" == typeof e) {
                    t = d(t, a + u);
                    var r = {
                        type: "POST",
                        url: a + u,
                        data: e,
                        dataType: "json",
                        error: t,
                        success: t
                    };
                    o.request(r)
                }
            },
            addMember: function(e, t) {
                t = d(t, a + s, 397742);
                var r = {
                    type: "POST",
                    url: a + s,
                    data: e,
                    dataType: "json",
                    error: t,
                    success: t
                };
                o.request(r)
            },
            setManage: function(e, t) {
                t = d(t, a + c, 397735);
                var r = {
                    type: "POST",
                    url: a + c,
                    data: e,
                    dataType: "json",
                    error: t,
                    success: t
                };
                o.request(r)
            },
            delMember: function(e, t) {
                t = d(t, a + deleteMember, 397741);
                var r = {
                    type: "POST",
                    url: a + deleteMember,
                    data: e,
                    dataType: "json",
                    error: t,
                    success: t
                };
                o.request(r)
            },
            setCard: function(e, t) {
                t = d(t, a + setMemberCard, 397736);
                var r = {
                    type: "POST",
                    url: a + setMemberCard,
                    data: e,
                    dataType: "json",
                    error: t,
                    success: t
                };
                o.request(r)
            },
            setMark: function(e, t) {
                t = d(t, a + setGroupTags, 397737);
                var r = {
                    type: "POST",
                    url: a + setGroupTags,
                    data: e,
                    dataType: "json",
                    error: t,
                    success: t
                };
                o.request(r)
            },
            setTags: function(e, t) {
                t = d(t)
            },
            addTag: function(e, t) {
                t = d(t, a + addMemberTag, 397738);
                var r = {
                    type: "POST",
                    url: a + addMemberTag,
                    data: e,
                    dataType: "json",
                    error: t,
                    success: t
                };
                o.request(r)
            },
            delTag: function(e, t) {
                t = d(t, a + delMemberTag, 397739);
                var r = {
                    type: "POST",
                    url: a + delMemberTag,
                    data: e,
                    dataType: "json",
                    error: t,
                    success: t
                };
                o.request(r)
            },
            editTag: function(e, t) {
                t = d(t, a + setMemberTag, 397740);
                var r = {
                    type: "POST",
                    url: a + setMemberTag,
                    data: e,
                    dataType: "json",
                    error: t,
                    success: t
                };
                o.request(r)
            },
            searchMember: u,
            getFace: function(e, t, r) {
                if ("object" == typeof e) {
                    t = d(t, faceurl),
                        new Date;
                    var a = faceurl,
                        n = {
                            url: (a += r ? "&qq=40|": "&grp=40|") + e.join("_"),
                            data: {
                                app: "group_info"
                            },
                            type: "GET",
                            dataType: "jsonp",
                            jsonp: "cb",
                            jsonpCallback: "face_cb_" + i++,
                            error: function(e, t, r) {},
                            success: function(e) {
                                t(e)
                            }
                        };
                    o.request(n)
                }
            }
        }
    } (window.QunHandler),
    function(e) {
        $(window.QunHandler.member);
        var s = window.QunHandler.member.model,
            c = window.QunHandler.util,
            l = ExtDialog,
            d = 0,
            u = !1;
        gidChange = !0,
            myGroup = null,
            loadEd = !1,
            nowGroup = null,
            myFriend = null,
            friend2Key = {},
            member2uin = {},
            inGroup = [],
            hasNext = 1,
            myQQ = $.getQQ(),
            myUin = $.getQQ(),
            nowKey = "",
            st = 0,
            count = 0,
            defNum = 20,
            end = defNum,
            nowDesc = 0,
            nowOrder = 0,
            nowFilter = {},
            nowTips = {},
            loading = 0,
            reseting = 0;
        var i = !(gList = {});
        $.browser && $.browser.version && "6.0" == $.browser.version && (u = !0);
        var m = {
                selectgroup: 12089,
                clicksearch: 12090,
                show: 12091,
                changegroup: 12092,
                addmember: 12093,
                setmanage: 12094,
                delmanage: 12095,
                delmemberbtn: 12096,
                delmemberico: 12097,
                search: 12098,
                editcard: 12099,
                orderq: 12100,
                jointime: 12101,
                orderlv: 12102,
                changetag: 12103,
                addtag: 12104,
                more: 12105,
                moresub: 12106,
                orderspeak: 12107
            },
            g = {
                "不限": -1,
                "不良记录成员": 1,
                "男": "0",
                "女": "1",
                "1年内": "0|1",
                "1年-3年": "1|3",
                "3年-5年": "3|5",
                "5-7年": "5|7",
                "7年以上": "7|0",
                "1个月内": "1|2592000",
                "1-3个月": "2592000|7776000",
                "3-6个月": "7776000|15552000",
                "6-12个月": "15552000|31536000",
                "12个月以上": "31536000|0"
            };
        function f(e, t) {
            if (t += "", "string" == typeof e) {
                var r = (o = new RegExp("(" + e.replace(/&nbsp;/g, " ") + ")", "i").exec(t)) && o[1] || "";
                return t.replace(/&nbsp;/g, " ").replace(r, '<span class="red">' + r + "</span>")
            }
            var a;
            for (var n in e) if (0 <= t.indexOf(e[n])) {
                a = e[n];
                var o;
                r = (o = new RegExp("(" + a.replace(/&nbsp;/g, " ") + ")", "i").exec(t)) && o[1] || "";
                return t.replace(/&nbsp;/g, " ").replace(r, '<span class="red">' + r + "</span>")
            }
            return t
        }
        function b(e) {
            return (e += "").replace(/\</gi, "&lt;").replace(/\>/gi, "&gt;")
        }
        function n(e) {
            if ("" != e) {
                var t = function(e) {
                    var t = [];
                    if (e = ("" + e).toUpperCase(), myGroup.create && 0 < myGroup.create.length) for (var r = 0,
                                                                                                          a = myGroup.create.length; r < a; r++) {
                        var n = myGroup.create[r],
                            o = {};
                        $.extend(o, n);
                        var i = o.gc + "";
                        o.gn = o.gn.replace(/&nbsp;/gi, " "),
                        (0 <= o.gn.toUpperCase().indexOf(e) || 0 <= i.toUpperCase().indexOf(e)) && (o.ogn = f(e, o.gn), o.ogc = f(e, o.gc), t.push(o))
                    }
                    if (myGroup.manage && 0 < myGroup.manage.length) for (var r = 0,
                                                                              a = myGroup.manage.length; r < a; r++) {
                        var n = myGroup.manage[r],
                            o = {};
                        $.extend(o, n);
                        var i = o.gc + "";
                        o.gn = o.gn.replace(/&nbsp;/gi, " "),
                        (0 <= o.gn.toUpperCase().indexOf(e) || 0 <= i.toUpperCase().indexOf(e)) && (o.ogn = f(e, o.gn), o.ogc = f(e, o.gc), t.push(o))
                    }
                    if (myGroup.join && 0 < myGroup.join.length) for (var r = 0,
                                                                          a = myGroup.join.length; r < a; r++) {
                        var n = myGroup.join[r],
                            o = {};
                        $.extend(o, n);
                        var i = o.gc + "";
                        o.gn = o.gn.replace(/&nbsp;/gi, " "),
                        (0 <= o.gn.toUpperCase().indexOf(e) || 0 <= i.toUpperCase().indexOf(e)) && (o.ogn = f(e, o.gn), o.ogc = f(e, o.gc), t.push(o))
                    }
                    return t
                } (e);
                if (0 < t.length) {
                    var r = $("#smartTmp").html(),
                        a = {
                            html: $.encodeHtml,
                            attr: $.encodeAttr,
                            list: t
                        },
                        n = $.tmp(r, a);
                    $(".search-smart").html(n).show(),
                        $(".search-smart li").bind("click",
                            function() {
                                var e = $(this).attr("data-id");
                                gidChange = !0,
                                    d = e,
                                    M(),
                                    l.hide()
                            })
                } else $(".search-smart").html("<li>没有找到符合条件的群</li>").show()
            } else $(".search-smart").html("").hide()
        }
        function a(e) {
            if ("" != e) {
                var t = function(e) {
                    var t = [];
                    for (var r in e = ("" + e).toUpperCase(), friend2Key) {
                        var a = friend2Key[r],
                            n = {};
                        $.extend(n, a);
                        var o = n.uin + "";
                        n.name = n.name.replace(/&nbsp;/gi, " "),
                        (0 <= n.name.toUpperCase().indexOf(e) || 0 <= "" + o.toUpperCase().indexOf(e)) && (n.oname = f(e, n.name), n.ouin = f(e, n.uin), t.push(n))
                    }
                    return t
                } (e);
                if (0 < t.length) {
                    var r = $("#friendsmartTmp").html(),
                        a = {
                            html: $.encodeHtml,
                            attr: $.encodeAttr,
                            avater: c.getAvatar,
                            list: t
                        },
                        n = $.tmp(r, a);
                    $(".search-smart").html(n).show(),
                        $(".search-smart li").bind("click",
                            function() {
                                q($(this).attr("data-id"))
                            })
                } else $(".search-smart").html("<li>没有找到符合条件的好友</li>").show()
            } else $(".search-smart").html("").hide()
        }
        function v(e) {
            var t = $("#myGroupTmp").html(),
                r = {
                    html: $.encodeHtml,
                    attr: $.encodeAttr,
                    data: e
                },
                a = $.tmp(t, r);
            l.show({
                title: "选择QQ群",
                width: "605px",
                height: "auto",
                html: a,
                handler: {
                    ".my-group-list li": {
                        click: function(e) {
                            var t = e.target;
                            "IMG" == t.nodeName && (t = t.parentNode),
                                gidChange = !0,
                                d = $(t).attr("data-id"),
                                $("#groupMember th").removeClass("th-select").removeClass("th-select1").removeClass("selected"),
                                G(),
                                M(),
                                $("i.arrow").attr("class", "arrow"),
                                l.hide()
                        },
                        mouseenter: function(e) {
                            var t = e.target;
                            "IMG" == t.nodeName && (t = t.parentNode),
                                $(t).addClass("hover")
                        },
                        mouseleave: function(e) {
                            var t = e.target;
                            "IMG" == t.nodeName && (t = t.parentNode),
                                $(t).removeClass("hover")
                        }
                    },
                    ".dialog-search-input": {
                        keyup: function(e) {
                            n($.trim($(this).val()))
                        },
                        focus: function(e) {
                            var t = $(this).attr("data-def"),
                                r = $(this).val();
                            r != t && "" != r || ($(".group-search-btn").attr("class", "group-search-btn icon-close"), $(this).val("").addClass("color"))
                        }
                    },
                    ".group-search-btn": {
                        click: function() {
                            var e = $(".dialog-search-input").attr("data-def");
                            $(".dialog-search-input").blur().val(e).removeClass("color"),
                                $(".group-search-btn").attr("class", "group-search-btn icon-search-btn"),
                                $(".search-smart").html("").hide()
                        }
                    },
                    ".icon-search-btn": {
                        click: function(e) {
                            if ($(this).hasClass("icon-search-btn-close")) {
                                $(this).removeClass("icon-search-btn-close");
                                var t = $(".dialog-search-input").attr("data-def");
                                $(".dialog-search-input").blur().val(t).removeClass("color")
                            }
                        }
                    }
                }
            }),
                loadEd = !0
        }
        function w(e) {
            var t = "//q4.qlogo.cn/g?b=qq&nk=" + e + "&s=140",
                r = new Image;
            r.onload = function() {
                $("#useIcon" + e).attr("src", t).removeClass("icon-deficon"),
                    r = null
            },
                r.src = t
        }
        function o(e) {
            for (var t = 0,
                     r = e.list.length; t < r; t++) w(e.list[t].uin);
            var a = $("#groupMemberTmp").html(),
                n = {
                    hasNormalRemove: i,
                    html: b,
                    attr: $.encodeAttr,
                    show: f,
                    time: c.formatTime,
                    avater: c.getAvatar
                };
            $.extend(n, e);
            var o = $.tmp(a, n);
            $("#groupMember").append(o),
                $("#groupMember tr").unbind().bind("mouseenter",
                    function(e) {
                        $("#groupMember tr").removeClass("tr-hover"),
                            "tr" == e.target.nodeName ? $(e.target).addClass("tr-hover") : $(e.target).parents("tr").addClass("tr-hover")
                    }).bind("mouseleave",
                    function(e) {
                        $(this).removeClass("tr-hover")
                    }),
                $("#groupMember .td-user-nick").unbind().bind("click",
                    function(e) {
                        $(e.target).hasClass("icon-group-manage") &&
                        function(t) {
                            var e = "" == member2uin[t].card ? member2uin[t].nick: member2uin[t].card;
                            l.alert("确定要取消 " + e + "(" + t + ") 的管理员资格吗？", {
                                width: 450,
                                buttons: {
                                    submit: {
                                        name: "确定",
                                        fn: function() {
                                            s.setManage({
                                                    gc: d,
                                                    ul: t,
                                                    op: 0
                                                },
                                                function(e) {
                                                    0 == e.ec ? ($(".manage" + t).removeClass("icon-group-manage").removeAttr("title").parent("a.group-manage-a").removeClass("group-manage-a"), $("#input" + t).attr("data-type", 2), nowGroup.adnum--, U()) : l.alert("系统繁忙，请稍后重试!", {
                                                        type: "fail",
                                                        buttons: {
                                                            submit: {
                                                                name: "确定",
                                                                fn: function() {
                                                                    l.hide()
                                                                }
                                                            }
                                                        }
                                                    })
                                                }),
                                                l.hide()
                                        }
                                    },
                                    close: {
                                        name: "取消",
                                        fn: function() {
                                            l.hide()
                                        }
                                    }
                                }
                            })
                        } ($(e.target).attr("data-id"))
                    })
        }
        function y(e) {
            return "//p.qlogo.cn/gh/" + e + "/" + e + "_1/40"
        }
        function t(e) {
            var t = 0;
            if (0 == e.ec) {
                var r = [];
                if (e.create) for (var a = 0,
                                       n = e.create.length; a < n; a++) 0 == a && (t = e.create[a].gc),
                    r.push(e.create[a].gc),
                    gList[e.create[a].gc] = e.create[a],
                    gList[e.create[a].gc].auth = 2,
                    gList[e.create[a].gc].face = y(e.create[a].gc);
                if (e.manage) for (a = 0, n = e.manage.length; a < n; a++) 0 != a || t || (t = e.manage[a].gc),
                    r.push(e.manage[a].gc),
                    gList[e.manage[a].gc] = e.manage[a],
                    gList[e.manage[a].gc].auth = 1,
                    gList[e.manage[a].gc].face = y(e.manage[a].gc);
                if (e.join) for (a = 0, n = e.join.length; a < n; a++) 0 != a || t || (t = e.join[a].gc),
                    r.push(e.join[a].gc),
                    gList[e.join[a].gc] = e.join[a],
                    gList[e.join[a].gc].auth = 0,
                    gList[e.join[a].gc].face = y(e.join[a].gc);
                if (20 < r.length) {
                    var o = [];
                    for (var a in r) 0,
                        o.length < 20 ? o.push(r[a]) : o = [];
                    o.length
                }
                0 == r.length && $("#selectAll").hide(),
                    d = $.getHash("gid"),
                    myGroup = {
                        create: e.create || [],
                        join: e.join || [],
                        manage: e.manage || [],
                        nums: r.length
                    },
                    0 < r.length ? d && gList[d] ? j(d) : (v(myGroup), j(t), d = t) : l.alert('您当前没有加入任何群，现在就去<a href="http://id.qq.com/index.html#qun-create" target="_blank">创建群</a>吧！', {
                        buttons: {
                            submit: {
                                name: "确定",
                                fn: function() {
                                    l.hide()
                                }
                            }
                        }
                    }),
                    $("#changeGroup").bind("click",
                        function(e) {
                            v(myGroup),
                                report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_turn", d])
                        })
            }
        }
        function _(e) {
            nowGroup.tag || (nowGroup.tag = {});
            for (var t = 0,
                     r = e.length; t < r; t++) {
                var a = e[t];
                nowGroup.tag[a.tag_id] = a.tag
            }
        }
        function k(e) {
            var t = $("#addTipsTmp").html(),
                r = {
                    html: $.encodeHtml,
                    attr: $.encodeAttr,
                    list: nowGroup.tag,
                    ie6: u
                },
                a = $.tmp(t, r),
                n = nowGroup.tag,
                o = [],
                i = [];
            l.confirm({
                title: "设置群标签",
                width: "346px",
                height: "auto",
                html: a,
                handler: {
                    ".tags-li": {
                        mouseenter: function() {
                            $(".tags-li").removeClass("selected"),
                                $(this).addClass("selected")
                        }
                    },
                    ".tags-input": {
                        blur: function() {
                            $(this)
                        }
                    },
                    ".icon-close": {
                        click: function() {
                            var e = $(this).attr("data-id"),
                                t = $(this).attr("data-name");
                            if (e) $.inArray(e, i) < 0 && i.push(e);
                            else {
                                var r = [];
                                for (var a in o) t != o[a] && r.push(t);
                                o = r
                            }
                            $(this).parents("li").remove()
                        }
                    },
                    ".group-tips-txt": {
                        keyup: function(e) {
                            var t = $(this).val();
                            24 < $.getLen(t) && (t = $.substr(t, 24), $(this).val(t))
                        }
                    },
                    ".input-btn": {
                        click: function() {
                            var e = $.trim($(".group-tips-txt").val());
                            "" != e && $.getLen(e) <= 24 && $.inArray(e, o) < 0 && !
                                function(e) {
                                    for (var t in nowGroup.tag) if (e == nowGroup.tag[t]) return ! 0;
                                    return ! 1
                                } (e) &&
                            function() {
                                var e = 0;
                                for (var t in nowGroup.tag) e++;
                                return e
                            } () + o.length < 20 && (o.push(e),
                                function(e) {
                                    var t = $("#oneTipsTmp").html(),
                                        r = {
                                            html: $.encodeHtml,
                                            attr: $.encodeAttr,
                                            name: e
                                        },
                                        a = $.tmp(t, r);
                                    $("#memberLabelList").append(a)
                                } (e), report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_addtag", d])),
                                $(".group-tips-txt").val("")
                        }
                    }
                },
                after: {},
                buttons: {
                    submit: {
                        name: "确定",
                        fn: function() {
                            var a = [];
                            if ($(".tags-input").each(function() {
                                var e = $(this),
                                    t = e.attr("data-id"),
                                    r = e.val();
                                r != n[t] && a.push({
                                    id: t,
                                    tag_id: t,
                                    tag: r
                                })
                            }), 0 < a.length) {
                                var e = {
                                    tags: a
                                };
                                s.editTag({
                                        gc: d,
                                        tags: JSON.stringify(e)
                                    },
                                    function(e) {
                                        0 === e.ec ? (_(a), report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_tag", d])) : l.alert("系统繁忙，请稍后重试!", {
                                            type: "fail",
                                            buttons: {
                                                submit: {
                                                    name: "确定",
                                                    fn: function() {
                                                        l.hide()
                                                    }
                                                }
                                            }
                                        })
                                    })
                            }
                            if (0 < o.length) {
                                e = {
                                    tags: o
                                };
                                s.addTag({
                                        gc: d,
                                        tag: JSON.stringify(e)
                                    },
                                    function(e) {
                                        0 == e.ec ? (report_core.prl(m.addtag), _(e.result), report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_tag", d])) : l.alert("系统繁忙，请稍后重试!", {
                                            type: "fail",
                                            buttons: {
                                                submit: {
                                                    name: "确定",
                                                    fn: function() {
                                                        l.hide()
                                                    }
                                                }
                                            }
                                        })
                                    })
                            }
                            if (0 < i.length) {
                                e = {
                                    tags: i
                                };
                                s.delTag({
                                        gc: d,
                                        tag_id: JSON.stringify(e)
                                    },
                                    function(e) {
                                        if (0 == e.ec) for (var t in e.tagid) delete nowGroup.tag[e.tagid[t]],
                                            $(".td-mark-old[data-type=" + e.tagid[t] + "] span").html("");
                                        else l.alert("系统繁忙，请稍后重试!", {
                                            type: "fail",
                                            buttons: {
                                                submit: {
                                                    name: "确定",
                                                    fn: function() {
                                                        l.hide()
                                                    }
                                                }
                                            }
                                        })
                                    })
                            }
                            l.hide()
                        }
                    },
                    close: {
                        name: "取消",
                        fn: function() {
                            l.hide()
                        }
                    }
                }
            })
        }
        function C() {
            $("#moreAction ul").each(function() {
                $(this).find(".query-filter").removeClass("selected").eq(0).addClass("selected")
            }),
                $("#moreAction input").val("");
            var e = $("#searchValue").attr("data-def");
            $("#searchValue").val(e).removeClass("color"),
                $("#selectResultTips").html(""),
                G(),
                R()
        }
        function G() {
            nowFilter = {},
                nowTips = {},
                nowKey = "",
                inGroup = [],
                nowDesc = 0,
                st = 0,
                end = defNum,
                N()
        }
        function T(e) {
            return nowGroup.num == nowGroup.adnum + 1 && gList[d] && gList[d].auth < 2 ? $("#selectAll").hide() : $("#selectAll").show(),
            $("#groupMember .list input[type=checkbox]").length || ($("#selectAll").hide(), $(".set-manage").hide(), $(".del-member").hide()),
                !!gList[e].auth
        }
        function M() {
            reseting = 1,
                st = 0,
                hasNext = 1,
                member2uin = {},
                end = defNum,
                $("#selectAll").prop({
                    checked: !1
                }),
                $(".group-select-btn").removeClass("selected"),
                R(),
                $("#groupMember tbody.list").remove(),
                j(d, nowFilter)
        }
        function j(e, t) {
            window.location.hash = "#gid=" + e;
            var r, a = gList[e],
                n = {}; (n = {
                gc: e,
                st: st,
                end: end,
                sort: nowDesc
            },
            "" != nowKey && (n.key = nowKey), t && t.last_speak_time) && (1 < (r = t.last_speak_time.split("|"))[0] && r[0] < 1e3 && (r[0] = 2592e3 * r[0]), 1 < r[1] && r[1] < 1e3 && (r[1] = 2592e3 * r[1]), t.last_speak_time = r[0] + "|" + r[1]);
            t && t.join_time && (1 < (r = t.join_time.split("|"))[0] && r[0] < 1e3 && (r[0] = 2592e3 * r[0]), 1 < r[0] && r[1] < 1e3 && (r[1] = 2592e3 * r[1]), t.join_time = r[0] + "|" + r[1]);
            $.extend(n, t),
                $("#groupTit").html(a.gn + "(" + a.gc + ")"),
                s.getMember(n, x)
        }
        function x(e) {
            if (loading = 0, (reseting = 0) == e.ec) {
                nowGroup = {
                    mems: e.mems,
                    lv: e.levelname,
                    tag: e.tag_info,
                    adnum: e.adm_num,
                    admax: e.adm_max,
                    num: e.count,
                    max: e.max_count
                },
                !st && gidChange &&
                function(e) {
                    gidChange = !1;
                    var t = {};
                    t = e.tag && e.lv ? {
                        member: 180,
                        card: 130,
                        qq: 90,
                        sex: 70,
                        qage: 70,
                        join: 110,
                        lv: 110
                    }: e.tag || e.lv ? {
                        member: 200,
                        card: 160,
                        qq: 100,
                        sex: 90,
                        qage: 90,
                        join: 120,
                        lv: 120
                    }: {
                        member: 200,
                        card: 170,
                        qq: 140,
                        sex: 105,
                        qage: 105,
                        join: 140
                    },
                        e.w = t;
                    var r = $("#groupMemberThTmp").html(),
                        a = e,
                        n = $.tmp(r, a);
                    $("#groupTh").html(n),
                        report_core.prl(m.selectgroup)
                } ({
                    show: T(d),
                    desc: nowDesc,
                    lv: nowGroup.lv,
                    tag: nowGroup.tag
                }),
                $.isEmptyObject(nowFilter) && "" == nowKey || 0 != st ||
                function(e) {
                    if ($("#groupSelectResult").removeClass("hide"), $.isEmptyObject(nowTips) && "" == nowKey) return;
                    var t = $("#searchResultTmp").html(),
                        r = {
                            html: $.encodeHtml,
                            attr: $.encodeAttr,
                            key: nowKey,
                            tips: nowTips,
                            num: e
                        },
                        a = $.tmp(t, r);
                    $.isEmptyObject(nowTips) ? $("#groupSelectResult").addClass("group-key-result") : $("#groupSelectResult").removeClass("group-key-result");
                    $("#groupSelectResult").html(a),
                    $.isEmptyObject(nowTips) || $("#groupSelectResult .icon-close").bind("click",
                        function(e) {
                            var t = $(this),
                                r = t.attr("data-key");
                            delete nowFilter[r],
                                delete nowTips[r],
                                $(".select-result" + r).remove(),
                                $("ul[data-key=" + r + "]").find("li").removeClass("selected").eq(1).addClass("selected"),
                                $("ul[data-key=" + r + "]").find("input").val(""),
                                M()
                        });
                    $("#groupSelectResult .reset-search").bind("click",
                        function() {
                            C(),
                                M()
                        })
                } (e.search_count),
                    nowGroup.lv ? ($(".th-lv").show(), $("#groupLevel").show()) : ($(".th-lv").hide(), $("#groupLevel").hide()),
                    nowGroup.tag ? $(".th-mark").show() : $(".th-mark").hide(),
                    count = e.count;
                var t = function(e) {
                    for (var t in myGroup.create) if (myGroup.create[t].gc == e) return "create";
                    for (var t in myGroup.manage) if (myGroup.manage[t].gc == e) return "manage";
                    return "join"
                } (d);
                if ("join" === t && (nowGroup.mems || []).forEach(function(e) {
                    e.rm && (i = !0)
                }),
                    function(e) {
                        var t = $("#groupLevelTmp").html(),
                            r = {
                                html: $.encodeHtml,
                                attr: $.encodeAttr,
                                list: e,
                                filter: nowFilter
                            },
                            a = $.tmp(t, r);
                        $("#groupLevel").html(a)
                    } (nowGroup.lv),
                    function(e) {
                        if (e) for (var t = 0,
                                        r = e.length; t < r; t++) {
                            var a = e[t];
                            member2uin[a.uin] = {
                                nick: a.nick,
                                card: a.card
                            }
                        }
                    } (nowGroup.mems), e.search_count) o({
                    list: nowGroup.mems,
                    my: myQQ,
                    st: st,
                    lv: nowGroup.lv,
                    tag: nowGroup.tag,
                    type: t,
                    ie6: u
                }),
                    $("#groupMember").show(),
                    $("#searchEmpty").hide();
                else {
                    $("#groupMember").hide();
                    var r = $("#groupMemberEmptyTmp").html();
                    $("#searchEmpty").html(r).show()
                } !
                    function(e) {
                        var t = $("#groupMemberTitTmp").html(),
                            r = {
                                html: $.encodeHtml,
                                attr: $.encodeAttr
                            };
                        $.extend(r, e);
                        var a = $.tmp(t, r);
                        $("#groupMemberTit").html(a)
                    } ({
                        type: t,
                        hasNormalRemove: i,
                        num: nowGroup.num,
                        all: nowGroup.max || 0
                    });
                var a = e.search_count || e.count;
                st;
                end >= a ? hasNext = 0 : hasNext = 1,
                st < a && (st = end + 1, end = st + defNum, end >= a && (end = a)),
                    T(d)
            } else l.alert("拉取成员列表出错，请稍后重试!")
        }
        function r(e) {
            0 == e.ec && (myFriend = e.result,
                function() {
                    for (var e in myFriend) {
                        var t = myFriend[e];
                        if (t.mems) for (var r = 0,
                                             a = t.mems.length; r < a; r++) {
                            var n = t.mems[r];
                            friend2Key[n.uin] = n
                        }
                    }
                } ())
        }
        function q(e) {
            "object" != typeof e && (e = [friend2Key[e]]);
            for (var t = [], r = 0, a = e.length; r < a; r++) {
                var n = e[r];
                $.inArray(n.uin, inGroup) < 0 && n.uin != myQQ && (t.push(n), inGroup.push(n.uin))
            }
            if (0 != t.length) {
                var o = $("#newGroupMemberTmp").html(),
                    i = {
                        html: b,
                        attr: $.encodeAttr,
                        avater: c.getAvatar,
                        list: t
                    },
                    s = $.tmp(o, i);
                $("#newGroupMemberList").append(s),
                    $("#newGroupMemberList .i-link").unbind().bind("click",
                        function() {
                            var e = $(this),
                                t = e.attr("data-id");
                            e.parents("li").remove(),
                                function(e) {
                                    for (var t = [], r = 0, a = inGroup.length; r < a; r++) inGroup[r] != e && t.push(inGroup[r]);
                                    inGroup = t
                                } (t)
                        })
            }
        }
        function A(e) {
            var n = [];
            if (e ? n.push(e) : $("#groupMember .check-input:checked").each(function() {
                var e = $(this).val();
                isNaN(e) || e == myUin || n.push($(this).val())
            }), 20 < n.length) return l.alert("批量删除不能超过20人上限！", {
                type: "alert",
                buttons: {
                    submit: {
                        name: "确定",
                        fn: function() {
                            l.hide()
                        }
                    }
                }
            }),
                void report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_grp", "del_tl", d]);
            var t = "";
            t = "确定将 " + ("" == member2uin[n[0]].card ? member2uin[n[0]].nick: member2uin[n[0]].card) + "(" + n[0] + ") ",
            1 < n.length && (t += "等" + n.length + "位成员"),
                t += "从本群中删除吗？",
            i || (1 == n.length ? t += '<br><p><input type="checkbox" id="notJoin" /> 不再接收此人加群申请</p>': 1 < n.length && (t += '<br><p><input type="checkbox" id="notJoin" /> 不再接收他们的加群申请</p>')),
                l.alert(t, {
                    width: 450,
                    buttons: {
                        submit: {
                            name: "确定",
                            fn: function() {
                                $("#notJoin").prop("checked") && report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_grp", "del_bl", d]),
                                    s.delMember({
                                            gc: d,
                                            ul: n.join("|"),
                                            flag: $("#notJoin").prop("checked") ? 1 : 0
                                        },
                                        function(e) {
                                            if (0 == e.ec) {
                                                for (var t = 0,
                                                         r = n.length; t < r; t++) $(".mb" + n[t]).remove();
                                                var a = nowGroup.num - n.length;
                                                a || (a = 1),
                                                    nowGroup.num = a,
                                                    $("#groupMember tr.mb").length ? ($("#groupMember tr.mb").each(function(e) {
                                                        $(this).find("td.td-no").text(e + 1)
                                                    }), $("#selectAll").prop({
                                                        checked: !1
                                                    }), $("#groupMemberNum").text(a), U(), T(d)) : (C(), M())
                                            } else l.alert("系统繁忙，请稍后重试!", {
                                                type: "fail",
                                                buttons: {
                                                    submit: {
                                                        name: "确定",
                                                        fn: function() {
                                                            l.hide()
                                                        }
                                                    }
                                                }
                                            })
                                        }),
                                    l.hide()
                            }
                        },
                        close: {
                            name: "取消",
                            fn: function() {
                                l.hide()
                            }
                        }
                    }
                })
        }
        function R() {
            $("#moreAction > div").addClass("hide"),
            $.isEmptyObject(nowFilter) && $("#moreAction input").val(""),
                $.isEmptyObject(nowTips) ? $("#groupSelectResult").addClass("hide") : $("#groupSelectResult").removeClass("hide")
        }
        var S = "";
        function L(e) {
            var t = $("#searchResultTipTmp").html(),
                r = {
                    html: $.encodeHtml,
                    attr: $.encodeAttr
                };
            nowTips[e.key] = e,
                $.extend(r, e),
                h = $.tmp(t, r),
                $(".select-result" + e.type).remove(),
                $("#selectResultTips").append(h),
                seleType = e.type
        }
        function U() {
            var e = $("#groupMember .list input:checked").length;
            if ($("#groupMember .list .check-input").length || $("#selectAll").hide(), 0 < $("#groupMember input:checked").length) {
                if ($("#selectAll").prop("checked") && 0 === e) $("#groupMemberTit .set-manage").addClass("disabled").prop({
                    disabled: !0
                }),
                    $("#groupMemberTit button:last-child").addClass("disabled").prop({
                        disabled: !0
                    });
                else if ($("#groupMember input:checked[data-type=1]").length == e) $("#groupMemberTit button").removeClass("disabled").prop({
                    disabled: !1
                }).eq(1).addClass("disabled").prop({
                    disabled: !0
                });
                else {
                    $("#groupMember input:checked[data-type=2]").length + nowGroup.adnum > nowGroup.admax ? $("#groupMemberTit button").removeClass("disabled").prop({
                        disabled: !1
                    }) : $("#selectAll").prop("checked") && 0 === e ? $("#groupMemberTit button:last-child").addClass("disabled").prop({
                        disabled: !0
                    }) : $("#groupMemberTit button").removeClass("disabled").prop({
                        disabled: !1
                    })
                }
            } else $("#groupMemberTit button").addClass("disabled").prop({
                disabled: !0
            }),
                $("#groupMemberTit .goto-me").removeClass("disabled").prop({
                    disabled: !1
                }),
                $("#groupMemberTit .add-member").removeClass("disabled").prop({
                    disabled: !1
                })
        }
        function N() {
            $.isEmptyObject(nowFilter) ? $("#groupSelectMoreBtm .clear").addClass("disabled").prop({
                disabled: !0
            }) : $("#groupSelectMoreBtm .clear").removeClass("disabled").prop({
                disabled: !1
            })
        }
        function D() {
            $("#searchValue").bind("focus",
                function(e) {
                    var t = $(this),
                        r = t.val(),
                        a = t.attr("data-def");
                    "" != r && r != a || t.val("").addClass("color")
                }).bind("blur",
                function(e) {
                    var t = $(this),
                        r = t.val(),
                        a = t.attr("data-def");
                    "" != r && r != a || t.val(a).removeClass("color")
                }).bind("keyup",
                function(e) {
                    13 == e.keyCode && $("#searchBtn").click()
                }),
                $("#searchBtn").bind("click",
                    function(e) {
                        var t = $.trim($("#searchValue").val()),
                            r = $("#searchValue").attr("data-def");
                        "" != t && t != r && (st = 0, end = defNum, nowKey = t, $("#selectAll").prop({
                            checked: !1
                        }), M(nowFilter), report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_find", d]))
                    }),
                $("#selectResultTips").bind("click",
                    function(e) {
                        var t = $(e.target);
                        t.hasClass("icon-close") && (type = t.attr("data-type"), key = t.attr("data-key"), t.parents("a.select-result").remove(), delete nowFilter[key], delete nowTips[key], $(".select-result" + type).remove(), $("ul[data-type=" + type + "]").find("li").removeClass("selected").eq(1).addClass("selected"), $("ul[data-type=" + type + "]").find("input").val(""), N())
                    }),
                $("#moreAction").on("click", ".query-filter",
                    function(e) {
                        var t = $(this),
                            r = t.parents("ul"),
                            a = r.attr("data-tag"),
                            n = r.attr("data-type"),
                            o = r.attr("data-key"),
                            i = t.attr("data-idx");
                        if (!t.hasClass("sl") && t.parents("li.sl")) {
                            r.find(".query-filter").removeClass("selected");
                            var s = $.trim(t.text());
                            r.find("input").val(""),
                                "不限" != s ? (L({
                                    tag: a,
                                    value: s,
                                    type: n,
                                    key: o
                                }), g[s] ? -1 != g[s] && (nowFilter[o] = g[s]) : nowFilter[o] = i) : (delete nowFilter[o], delete nowTips[o], $(".select-result" + n).remove()),
                                t.addClass("selected")
                        }
                        N()
                    }),
                $("#moreAction input[type=text]").bind("blur",
                    function(e) {
                        var t = $(this),
                            r = t.parents("ul"),
                            a = r.attr("data-tag"),
                            n = r.attr("data-type"),
                            o = r.attr("data-key"),
                            i = t.parents("li").attr("data-val"),
                            s = [];
                        r.find("input").each(function(e) {
                            "" != $(this).val() ? (s[e] = parseInt($(this).val()), e && s[e - 1] >= s[e] && (s[e] = s[e - 1] + 1, r.find("input").eq(e).val(s[e]))) : e ? (s[e] = s[e - 1] + 1, $(this).val(s[e])) : (s[e] = 0, $(this).val(0))
                        }),
                        2 == s.length && (r.find(".query-filter").removeClass("selected"), L({
                            tag: a,
                            value: s[0] + "-" + s[1] + i,
                            type: n,
                            key: o
                        }), nowFilter[o] = s[0] + "|" + s[1]),
                            N()
                    }),
                $("#groupSelectMoreBtm .clear").bind("click",
                    function() {
                        $("#moreAction .query-filter").removeClass("selected"),
                            $("#moreAction ul").each(function() {
                                $(this).find(".query-filter").eq(0).addClass("selected")
                            }),
                            $("#moreAction input").val(""),
                            $("#selectResultTips").html(""),
                            nowFilter = {},
                            nowTips = {},
                            N()
                    }),
                $("#groupSelectMoreBtm .submit").bind("click",
                    function() {
                        switch ($(".group-select-btn").removeClass("selected"), $("#selectAll").prop({
                            checked: !1
                        }), M(), R(), S) {
                            case "0":
                                report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1", "ver1"], [myUin, +new Date, "Grp_website", "mana_mber", "choose_suc", d, 0]);
                                break;
                            case "1":
                                report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1", "ver1"], [myUin, +new Date, "Grp_website", "mana_mber", "choose_suc", d, 1]);
                                break;
                            case "2":
                                report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1", "ver1"], [myUin, +new Date, "Grp_website", "mana_mber", "choose_suc", d, 2]);
                                break;
                            case "3":
                                report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1", "ver1"], [myUin, +new Date, "Grp_website", "mana_mber", "choose_suc", d, 3]);
                                break;
                            case "4":
                                report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1", "ver1"], [myUin, +new Date, "Grp_website", "mana_mber", "choose_suc", d, 4]);
                                break;
                            case "5":
                                report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1", "ver1"], [myUin, +new Date, "Grp_website", "mana_mber", "choose_suc", d, 5])
                        }
                    }),
                $(".group-select-btn").bind("click",
                    function() {
                        $(this).hasClass("selected") ? ($(this).removeClass("selected"), R()) : ($(this).addClass("selected"), $("#moreAction div.child").addClass("hide"), $("#moreAction .group-select-more").removeClass("hide")),
                            report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_choose", d])
                    }),
                $(document).on("click", ".td-mark",
                    function(e) {
                        var t = $(e.target);
                        if (t.parents(".td-mark-old").length || t.hasClass("td-mark-old")) {
                            "td" != e.target.nodeName && (t = t.parents("td.td-mark"));
                            var r = t.attr("data-tags");
                            $(this).find(".td-mark-old").hide(),
                                function(e, t) {
                                    var r = $("#tagMenuTmp").html(),
                                        a = {
                                            html: $.encodeHtml,
                                            attr: $.encodeAttr,
                                            list: nowGroup.tag,
                                            tag: e
                                        },
                                        n = $.tmp(r, a);
                                    t.append(n)
                                } (r, t)
                        } else if (t.parents(".mark-select-add").length || t.hasClass("mark-select-add")) k(t.parents(".td-mark-old"));
                        else if (!t.hasClass("td-mark")) { !
                            function(e, t, r) {
                                var a = nowGroup.tag[e] || "";
                                s.setMark({
                                        gc: d,
                                        tag_id: e,
                                        uin_list: t
                                    },
                                    function(e) {
                                        0 == e.ec ? r.find(".td-mark-old span").text(a) : 10 < e.ec && e.ec < 15 ? l.alert("你输入的内容中含有敏感词，请您重新输入!", {
                                            type: "fail",
                                            buttons: {
                                                submit: {
                                                    name: "确定",
                                                    fn: function() {
                                                        l.hide()
                                                    }
                                                }
                                            }
                                        }) : l.alert("系统繁忙，请稍后重试!", {
                                            type: "fail",
                                            buttons: {
                                                submit: {
                                                    name: "确定",
                                                    fn: function() {
                                                        l.hide()
                                                    }
                                                }
                                            }
                                        }),
                                            r.find(".td-mark-old").show(),
                                            r.find(".mark-div").remove()
                                    })
                            } (t.attr("data-idx"), t.parents(".td-mark").attr("data-id"), t.parents(".td-mark"))
                        }
                    }),
                $("#groupMember").on("click", ".icon-close",
                    function() {
                        A($(this).attr("data-id")),
                            report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_onedel", d])
                    }),
                $("#groupMember").on("click", ".group-card",
                    function(e) {
                        var t = $(this).parent("span");
                        t.addClass("hover"),
                            function(e) {
                                e.focus();
                                var t = e[0];
                                document.selection || (t.selectionStart = 0, t.selectionEnd = 0, t.focus())
                            } (t.find("input.member-card"))
                    }),
                $("#groupMember").on("keyup", ".member-card",
                    function() {
                        var e = $(this),
                            t = $.trim(e.val());
                        21 < $.getLen(t) && (t = $.substr(t, 21)),
                            $(this).val(t)
                    }).on("blur", ".member-card",
                    function(e) {
                        var t = $(e.target),
                            r = $(e.target).attr("data-id"),
                            a = t.attr("data-key"),
                            n = t.attr("data-old"),
                            o = $.trim(t.val());
                        21 < $.getLen(o) && (o = $.substr(o, 21)),
                            t.parents("span").removeClass("hover"),
                            function(t, r, a, n) {
                                var e = {
                                    gc: d,
                                    u: t
                                };
                                "" != r && (e.name = r),
                                    s.setCard(e,
                                        function(e) {
                                            0 == e.ec ? (report_core.prl(m.editcard), a ? $(".group-card" + t).html(f(a, r)) : $(".group-card" + t).text(r), $("#member-card" + t).val(r), n && $("#member-card" + t).attr("data-old", a)) : 10 < e.ec && e.ec < 15 ? l.alert("你输入的内容中含有敏感词，请您重新输入!", {
                                                type: "fail",
                                                buttons: {
                                                    submit: {
                                                        name: "确定",
                                                        fn: function() {
                                                            l.hide()
                                                        }
                                                    }
                                                }
                                            }) : ($("#member-card" + t).val(n), l.alert("系统繁忙，请稍后重试!", {
                                                type: "fail",
                                                buttons: {
                                                    submit: {
                                                        name: "确定",
                                                        fn: function() {
                                                            l.hide()
                                                        }
                                                    }
                                                }
                                            }))
                                        })
                            } (r, o, a, n),
                            report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_name", d])
                    }).on("focus", ".member-card",
                    function() {
                        $(this).parents("span").addClass("hover")
                    }),
                $("#groupMember").on("click", "th.th-desc",
                    function(e) {
                        $("#groupMember th.th-desc").removeClass("selected");
                        var t = $(e.target),
                            r = t.attr("idx");
                        t.attr("idx"),
                            t.attr("cmd");
                        "th" != e.target.nodeName ? p = t.parents("th") : p = t,
                            t.parents("ul.group-desc-arrow").length ? ($("#groupMember th.th-desc").removeClass("th-select").removeClass("th-select1"), $("#groupMember .group-desc > span").removeClass("span-select"), "li" != e.target.nodeName && (tp = t.parents("li"), r = tp.attr("idx"), tp.attr("idx"), tp.attr("cmd"), data_tag = tp.attr("data-tag")), r ? ($("#groupMember i.arrow").attr("class", "arrow"), parseInt(r) % 2 ? (p.find("i.arrow").attr("class", "arrow icon-arrow-desc-red"), p.addClass("th-select1"), report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1", "ver1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_" + data_tag, d, 1])) : (p.find("i.arrow").attr("class", "arrow icon-arrow-desc1-red"), p.addClass("th-select"), report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1", "ver1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_" + data_tag, d, 2])), p.find(".group-desc > span").addClass("span-select")) : (r = 0, p.find("i.arrow").attr("class", "arrow"), p.find(".group-desc > span").removeClass("span-select"), data_tag = p.find(".group-ff > .group-desc > .group-desc-arrow > li").attr("data-tag"), report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1", "ver1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_" + data_tag, d, 0])), nowDesc = r, M()) : (p.addClass("selected"), p.find("ul.group-desc-arrow li").show(), "arrow" != p.find("i.arrow").attr("class") ? p.find("i.arrow").hasClass("icon-arrow-desc1-red") ? p.find("ul.group-desc-arrow li").eq(2).hide() : p.find("ul.group-desc-arrow li").eq(1).hide() : p.find("ul.group-desc-arrow li").eq(0).hide())
                    }),
                $("#groupMember").on("click", "#selectAll",
                    function(e) {
                        $(this).prop("checked") ? $("tbody.list input[type=checkbox]").prop({
                            checked: !0
                        }) : $("tbody.list input[type=checkbox]").prop({
                            checked: !1
                        }),
                            U()
                    }),
                $("#groupMember").on("click", "tbody.list .check-input",
                    function(e) {
                        if (0 !== $("#groupMember tbody.list .check-input").length) {
                            var t = !0;
                            $("#groupMember tbody.list .check-input").each(function() {
                                var e = $(this);
                                isNaN(e.val()) || e.attr("checked") || (t = !1)
                            }),
                                t ? $("#selectAll").prop({
                                    checked: !0
                                }) : $("#selectAll").prop({
                                    checked: !1
                                })
                        }
                    }),
                $("#groupMember").on("click", ".list input[type=checkbox]",
                    function(e) {
                        U()
                    }),
                $("body").bind("click",
                    function(e) {
                        var t = $(e.target),
                            r = $(e.target),
                            a = r.attr("data-tag"),
                            n = r.attr("data-idx") || 0;
                        if (m[a] && report_core.prl(m[a], n), 0 == t.parents("th.th-desc").length && $("#groupMember th.th-desc").removeClass("desc").removeClass("undesc").removeClass("selected"), 0 == t.parents("div.mark-div").length && ($(".mark-div").remove(), $(".td-mark-old").show()), 0 == t.parents(".search-smart").length && 0 == t.parents(".input-search").length) {
                            var o = $(".dialog-search-input").attr("data-def");
                            $(".dialog-search-input").blur().val(o).removeClass("color"),
                                $(".group-search-btn").attr("class", "group-search-btn icon-search-btn"),
                                $(".search-smart").html("").hide()
                        }
                    }),
                $(window).bind("scroll",
                    function(e) {
                        var t = $(this).scrollTop();
                        window.document.body.scrollHeight - 150 < $(this).height() + t && (st < count && !loading && !reseting && (loading = 1, hasNext && j(d, nowFilter))),
                            e.preventDefault()
                    }),
                $("#groupMemberTit").bind("click",
                    function(e) {
                        switch ($(e.target).attr("cmd")) {
                            case "add":
                                !
                                    function() {
                                        var e = $("#myFriendTmp").html(),
                                            t = {
                                                html: $.encodeHtml,
                                                attr: $.encodeAttr,
                                                avater: c.getAvatar,
                                                list: myFriend
                                            },
                                            r = $.tmp(e, t);
                                        l.confirm({
                                            title: "选择好友",
                                            width: "605px",
                                            height: "auto",
                                            html: r,
                                            handler: {
                                                ".dialog-search-input": {
                                                    keyup: function(e) {
                                                        a($.trim($(this).val()))
                                                    },
                                                    focus: function(e) {
                                                        var t = $(this).attr("data-def"),
                                                            r = $(this).val();
                                                        r != t && "" != r || ($(".group-search-btn").attr("class", "group-search-btn icon-close"), $(this).val("").addClass("color"))
                                                    }
                                                },
                                                ".group-search-btn": {
                                                    click: function() {
                                                        var e = $(".dialog-search-input").attr("data-def");
                                                        $(".dialog-search-input").blur().val(e).removeClass("color"),
                                                            $(".group-search-btn").attr("class", "group-search-btn icon-search-btn"),
                                                            $(".search-smart").html("").hide()
                                                    }
                                                }
                                            },
                                            after: function() {
                                                $("#myFriendList .friend-group-name").bind("click",
                                                    function(e) {
                                                        var t = $(this),
                                                            r = parseInt(t.attr("data-child")),
                                                            a = t.attr("data-show"),
                                                            n = $(e.target);
                                                        n.hasClass("icon-friend-join") ? q(myFriend[n.attr("data-idx")].mems) : r && (a ? (t.find(".icon-arrow-gray").attr("class", "icon-arrow-left"), t.next("ul").hide(), t.removeAttr("data-show").removeClass("open")) : (t.next("ul").show(), t.attr("data-show", 1).addClass("open"), t.find(".icon-arrow-left").attr("class", "icon-arrow-gray")))
                                                    }),
                                                    $("#myFriendList .friend").bind("click",
                                                        function(e) {
                                                            q($(this).attr("data-id"))
                                                        })
                                            },
                                            buttons: {
                                                submit: {
                                                    name: "确定",
                                                    fn: function() {
                                                        0 < inGroup.length && s.addMember({
                                                                gc: d,
                                                                ul: inGroup.join("|")
                                                            },
                                                            function(e) {
                                                                0 !== e.ec && l.alert("系统繁忙，请稍后重试!", {
                                                                    type: "fail",
                                                                    buttons: {
                                                                        submit: {
                                                                            name: "确定",
                                                                            fn: function() {
                                                                                l.hide()
                                                                            }
                                                                        }
                                                                    }
                                                                })
                                                            }),
                                                            l.hide()
                                                    }
                                                },
                                                close: {
                                                    name: "取消",
                                                    fn: function() {
                                                        l.hide()
                                                    }
                                                }
                                            }
                                        })
                                    } (),
                                    report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_add", d]);
                                break;
                            case "set":
                                !
                                    function() {
                                        var n = [],
                                            e = "";
                                        $("#groupMember .check-input:checked").each(function() {
                                            var e = $(this).val(),
                                                t = $(this).attr("data-type");
                                            e != myUin && t && 1 != t && n.push($(this).val())
                                        }),
                                            n.length + nowGroup.adnum > nowGroup.admax ? l.alert("群管理员人数已达上限，您可以增加群管理人数后再进行设置", {
                                                width: 450,
                                                buttons: {
                                                    submit: {
                                                        name: "确定",
                                                        fn: function() {
                                                            l.hide()
                                                        }
                                                    }
                                                }
                                            }) : (e = "确定要设置 " + ("" == member2uin[n[0]].card ? member2uin[n[0]].nick: member2uin[n[0]].card) + "(" + n[0] + ") ", 1 < n.length && (e += "等" + n.length + "位成员"), e += "为管理员吗？", l.alert(e, {
                                                width: 450,
                                                buttons: {
                                                    submit: {
                                                        name: "确定",
                                                        fn: function() {
                                                            s.setManage({
                                                                    gc: d,
                                                                    ul: n.join("|"),
                                                                    op: 1
                                                                },
                                                                function(e) {
                                                                    if (0 == e.ec) {
                                                                        for (var t = e.ul.split("|"), r = 0, a = t.length; r < a; r++) $(".manage" + t[r]).addClass("icon-group-manage").attr("title", "取消管理员").parent("a").attr("data-tag", "delmanage").addClass("group-manage-a"),
                                                                            $("#input" + t[r]).attr("data-type", 1);
                                                                        nowGroup.adnum += n.length,
                                                                            U(),
                                                                            report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_un", d])
                                                                    } else l.alert("系统繁忙，请稍后重试!", {
                                                                        type: "fail",
                                                                        buttons: {
                                                                            submit: {
                                                                                name: "确定",
                                                                                fn: function() {
                                                                                    l.hide()
                                                                                }
                                                                            }
                                                                        }
                                                                    })
                                                                }),
                                                                l.hide()
                                                        }
                                                    },
                                                    close: {
                                                        name: "取消",
                                                        fn: function() {
                                                            l.hide()
                                                        }
                                                    }
                                                }
                                            }))
                                    } (),
                                    report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_set", d]);
                                break;
                            case "del":
                                A(),
                                    report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_mber", "Clk_moredel", d])
                        }
                    })
        }
        e.view = {
            init: function() {
                1100 < document.body.clientHeight && (defNum = 40, end = 40),
                    report_core.prl(m.show),
                    report_core.tdwReport(["uin", "ts", "opername", "module", "action", "obj1"], [myUin, +new Date, "Grp_website", "mana_mber", "exp", d]),
                    s.getGroup(t),
                    s.getFriend(r),
                    D()
            }
        }
    } (window.QunHandler.member),
    function() {
        var t = $(window.QunHandler);
        $("#memberList tr").bind("mouseenter",
            function(e) {
                $("#memberList tr").removeClass("tr-hover"),
                    $(this).addClass("tr-hover")
            }).bind("mouseleave",
            function(e) {
                $(this).removeClass("tr-hover"),
                    $(this).find(".selected").removeClass("selected")
            }),
            $("#memberList .td-card").bind("click",
                function() {
                    $(this).find("input").addClass("selected")
                }),
            $("#memberList .td-mark").bind("click",
                function() {});
        var r = QunHandler.member.view;
        t.bind("loginEd",
            function(e) {
                r.init()
            }),
            t.bind("notLogin",
                function(e) {
                    t.triggerHandler("login:tologin", 1)
                }),
        $.isLogin() || t.triggerHandler("login:tologin", 1),
            report_core.monitor(397730)
    } ();