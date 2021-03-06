<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE ncx PUBLIC "-//NISO//DTD ncx 2005-1//EN" "http://www.daisy.org/z3986/2005/ncx-2005-1.dtd">
<ncx xmlns="http://www.daisy.org/z3986/2005/ncx/" version="2005-1" xml:lang="zh-cn">
    <head>
        <meta name="dtb:uid" content="79825d33-641e-4235-8e1b-596c7d57c94a"/>
        <meta name="dtb:depth" content="1"/>
        <meta name="dtb:totalPageCount" content="0"/>
        <meta name="dtb:maxPageNumber" content="0"/>
        <meta name="dtb:generator" content=" "/>
    </head>
    <docTitle>
        <text>${article.title}</text>
    </docTitle>
    <docAuthor>
        <text>${article.author} </text>
    </docAuthor>
    <navMap>
        <navPoint id="toc" playOrder="-1">
            <navLabel>
                <text>目录:${article.title}</text>
            </navLabel>
            <content src="catalog.htm"/>
        </navPoint>
        <#list article.chapters as chapter>
            <navPoint id="chaper_${chapter_index}" playOrder="${chapter_index}">
                <navLabel>
                    <text>${chapter}</text>
                </navLabel>
                <content src="html/${chapter_index}.html"/>
            </navPoint>
        </#list>

    </navMap>
</ncx>
