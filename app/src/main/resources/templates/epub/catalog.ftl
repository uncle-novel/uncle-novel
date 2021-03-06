<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="zh-CN">
<head>
    <title>${article.title}</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" type="text/css" href="style.css"/>
</head>
<body>
<h2>${article.title}</h2>
<div class="toc">
    <#list article.chapters as chapter>
        <div><a href="html/${chapter_index}.html">${chapter}</a></div>
    </#list>
</div>
</body>
</html>
