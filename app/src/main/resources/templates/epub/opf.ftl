<?xml version="1.0" encoding="utf-8"?>
<#assign catalogName='catalog'>
<package xmlns="http://www.idpf.org/2007/opf" version="2.0" unique-identifier="FoxUUID">
    <metadata xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:opf="http://www.idpf.org/2007/opf">
        <dc:title>${article.title}</dc:title>
        <dc:identifier opf:scheme="uuid" id="FoxUUID">79825d33-641e-4235-8e1b-596c7d57c94a</dc:identifier>
        <dc:creator></dc:creator>
        <dc:publisher></dc:publisher>
        <dc:language>zh-cn</dc:language>
        <meta name="cover" content="cover"/>
    </metadata>
    <manifest>
        <item id="cover" media-type="image/jpeg" href="cover.jpeg"/>
        <item id="ncx" media-type="application/x-dtbncx+xml" href="ncx.ncx"/>
        <item id="catalog" media-type="application/xhtml+xml" href="catalog.htm"/>
        <#list article.chapters as chapter>
            <item id="chapter_${chapter_index}" media-type="application/xhtml+xml" href="html/${chapter_index}.html"/>
        </#list>
    </manifest>

    <spine toc="ncx">
        <itemref idref="catalog"/>
        <#list article.chapters as chapter>
            <itemref idref="chapter_${chapter_index}"/>
        </#list>
    </spine>

    <guide>
        <reference type="text" title="正文" href="html/0.html"/>
        <reference type="toc" title="目录" href="catalog.htm"/>
    </guide>

</package>

