.root{
-fx-base: ${bgColor};
-fx-background: -fx-base;
-fx-header-background:${headerColor};
<#--背景不透明 头不透明-->
<#if fontColor=="">
    <#if bgColor!="transparent">
        text-color: ladder(-fx-base, rgba(255, 255, 255, 0.87) 49%, rgba(0, 0, 0, 0.87) 50%);
        <#if headerColor=="transparent">
            header-text-color: text-color;
        <#else>
            header-text-color: ladder(-fx-header-background, rgba(255, 255, 255, 0.87) 49%, rgba(0, 0, 0, 0.87) 50%);
        </#if>
    <#else>
        <#if headerColor=="transparent">
            header-text-color: text-color;
        <#else>
            header-text-color: ladder(-fx-header-background, rgba(255, 255, 255, 0.87) 49%, rgba(0, 0, 0, 0.87) 50%);
        </#if>
    </#if>
<#else>
    text-color: ${fontColor};
    header-text-color: text-color;
</#if>
border-color: ladder(-fx-base, rgba(255, 255, 255, 0.3) 49%, rgba(0, 0, 0, 0.3) 50%);
hover-bg-color: #009688;
hover-text-color:  rgba(255, 255, 255, 0.87);
selected-bg-color:  linear-gradient(to left, rgb(31, 212, 174), rgb(30, 205, 148));
selected-text-color:  ladder(selected-bg-color, rgba(255, 255, 255, 0.87) 49%, rgba(0, 0, 0, 0.87) 50%);
}

