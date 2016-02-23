# _`ExSport`_ build of _`PS3MediaServer`_ with new features added #
`¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯`
## What was added (synced with <a href='https://code.google.com/p/ps3mediaserver/source/list'><code>r652</code></a>): ##
  * **!!-- Fix 23.976/25fps A/V Mismatch --!!** accessible directly from **#TRANSCODE#** folder
  * Enabled automatic A/V synchronization when audio delay detected
  * Support for Audio/Subtitle tags(metadata/description) - resetting medialibrary needed for updating database structure!
  * Forced subtitles support (you can define forced subs tag so subs can be detected and played automatically)
  * Internal/External VOBSUB support (only solution how to render 3D subtitles), check <a href='http://541B0A7B.cm-5-4a.dynamic.ziggo.nl/3DSubtitler'>3DSubtitler</a>
  * Added support for custom MEncoder options on renderer level (not only as global settings) useful for quality settings, adding black bars, deinterlace, etc. working on renderer level
  * Partial-content (byte range) content size fix (WoH [issue #822](https://code.google.com/p/pms-exsport/issues/detail?id=#822) ) - support for chunking renderers
  * tomeko patch for specific renderers where Mediainfo=true doesn't work, check <a href='http://www.ps3mediaserver.org/forum/viewtopic.php?f=15&t=9882&start=20#p45892'>Forum</a>
  * When **`expand=`** string exists in **`CustomMencoderOptions`** and DVD format is played, whole string is ignored to avoid corrupted picture on TVs (eg. Panasonic TV)
  * **`ForcedSubs`** null pointer exception fixed
| **More info on WIKI page:** | **[FAQ](FAQ.md)** |
|:----------------------------|:------------------|

**_Please post feedback in forum:_**
<a href='http://www.ps3mediaserver.org/forum/viewtopic.php?f=14&t=11261'>Forum</a>