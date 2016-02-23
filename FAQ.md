# Some tips how to use new features #
**(Info here is quite old but principles are still same and applicable to UMS or PMS)**

### PMS.conf file ###
You can define **`forced_sub_tags`** in PMS.conf **(can be done in GUI in latest version!)** to specify tags which are used for **`forced`** subtitles.<br><br>
<b>Example:</b>
<pre><code># detect forced subtitle track (detection done by keywords in subtitle tag/description)<br>
# partial search works so defining "sing" will find also "singing"<br>
# when external subtitle file used, use "-" as delimiter. Example: moviename.en-forced.srt<br>
forced_sub_tags=forced,singing,titles<br>
</code></pre>
If this keyword is omitted, <b><code>forced</code></b> tag is used as default.<br>
<br>
<h3>Renderer.conf</h3>
You can define renderer specific keywords which works on renderer level<br><br>
<b>Examples:</b>
<pre><code># You can define specific filters working on renderer level<br>
# It means you can now easily define deinterlacer for Samsung TV and upscaler for PS3 etc.<br>
# Please use escaping by using "\" character when special char used<br>
# When this keyword is not defined, global settings in PMS has precedence<br>
# Example below adds black bars to fix aspect ratio problems on most TV like Panasonic, Samsung, Sony etc.<br>
CustomMencoderOptions=-vf softskip,expand=::::1:16\/9:4<br>
# CustomMencoderOptions still not implemented in official build v1.50 beta1<br>
<br>
# You can set different quality settings for every renderer separately.<br>
# It works better like bandwidth limiting because it is not so CPU aggresive<br>
CustomMencoderQualitySettings=keyint=5:vqscale=1:vqmin=3:vqmax=5<br>
<br>
# Show audio and subtitle tag for every track in #TRANSCODE# folder, if defined<br>
# It is useful to diferentiate between documentary audio or subtitle track, full or forced subtitles etc.<br>
# When not defined, TRUE is used as default (Metadata tags are retrieved by MediaInfo/FFmpeg engine)<br>
ShowAudioMetadata=false<br>
ShowSubMetadata=true<br>
<br>
# When true, PMS pass DLNA compliancy when MediaInfo=true<br>
# Default behavior of PMS is not DLNA compliant so almost all rendereres doesn't work correctly without this fix<br>
# PMS counts all files in folder before sent to renderer so total count is known to renderer = more compatible<br>
AnalyzeAllBeforeFolderShown=true<br>
# Deprecated(renamed) in official build v1.50 Beta1 and above<br>
</code></pre>

<h2>How to configure forced subtitles:</h2>
<i>To define tag for external subtitle file use this example:</i><br>
<b><i>moviename.cs-full.srt</i></b> or <b><i>moviename.cs-forced.srt</i></b> etc. Delimiter for tag is "<b>-</b>"<br>
For tagging subtitles in MKV container(if tag is missing), you can use e.g. <b><i>MKVTOOLNIX</i></b> and add this info as "<b>Track name</b>".<br><br>
<b>How to set preferred language for <code>Forced</code> subtitles in PMS:</b><br>
<b><code>Subtitles language priority:</code></b> - here put required subtitle code (only one supported!)<br>
GUI changed in official v1.50b1 to more easily configure forced subs so it is defined separately(in past "Subtitles lang priority:" was used for it)<br>
<br><br>
<img src='http://pms-exsport.googlecode.com/svn/wiki/images/pms_config.jpg' /><br><br>
<b>When correctly configured, how to load subs automatically?</b>
<ul><li>you need to enable "<b><code>Autoload *.srt/*.sub subtitles with the same name</code></b>" in PMS<br>
</li><li>and choose <b><code>[MENCODER]</code></b> or <b><code>[MENCODER]{External Subtitles}</code></b> inside <b><code>#--TRANSCODE--#</code></b> folder<br>
</li><li>or choose <b><code>Movie_Name.avi[MENCODER]{External Subtitles}</code></b> outside <b><code>#--TRANSCODE--#</code></b> folder<br>
</li><li>when <b><code>TSmuxer</code></b> engine is used, you need browse to <b><code>#--TRANSCODE--#</code></b> folder to fire up <b><code>MEncoder</code></b> engine</li></ul>

<h3>Preview how it is shown on UPnP/DLNA Client</h3>
<b><i>Local files shared in PMS:</i></b>
<pre><code>Sample.mkv<br>
Sample.de.idx (VobSub)<br>
Sample.de.sub (VobSub)<br>
Sample.es-Full.srt<br>
Sample.es-Headings.srt<br>
</code></pre>
<img src='http://pms-exsport.googlecode.com/svn/wiki/images/sample_small.png' />