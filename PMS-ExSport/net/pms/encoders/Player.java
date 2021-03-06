/*
 * PS3 Media Server, for streaming any medias to your PS3.
 * Copyright (C) 2008  A.Brochard
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 2
 * of the License only.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package net.pms.encoders;

import java.io.File;
import java.io.IOException;
import java.lang.UnsupportedOperationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JComponent;

import net.pms.configuration.PmsConfiguration;
import net.pms.configuration.RendererConfiguration;
import net.pms.dlna.DLNAMediaAudio;
import net.pms.dlna.DLNAMediaInfo;
import net.pms.dlna.DLNAMediaSubtitle;
import net.pms.dlna.DLNAResource;
import net.pms.external.ExternalFactory;
import net.pms.external.ExternalListener;
import net.pms.external.FinalizeTranscoderArgsListener;
import net.pms.formats.Format;
import net.pms.io.OutputParams;
import net.pms.io.ProcessWrapper;
import net.pms.PMS;
import net.pms.util.FileUtil;
import net.pms.util.Iso639;

public abstract class Player {
    public static final int VIDEO_SIMPLEFILE_PLAYER = 0;
    public static final int AUDIO_SIMPLEFILE_PLAYER = 1;
    public static final int VIDEO_WEBSTREAM_PLAYER = 2;
    public static final int AUDIO_WEBSTREAM_PLAYER = 3;
    public static final int MISC_PLAYER = 4;
    public static final String NATIVE = "NATIVE"; //$NON-NLS-1$
    public abstract int purpose();
    public abstract JComponent config();
    public abstract String id();
    public abstract String name();
    public abstract int type();
    public abstract String [] args();
    public abstract String mimeType();
    public abstract String executable();

    private static List<FinalizeTranscoderArgsListener> finalizeTranscodeArgsListeners =
	new ArrayList<FinalizeTranscoderArgsListener>();

    public static void initializeFinalizeTranscoderArgsListeners() {
	for (ExternalListener listener : ExternalFactory.getExternalListeners()) {
	    if (listener instanceof FinalizeTranscoderArgsListener) {
		finalizeTranscodeArgsListeners.add((FinalizeTranscoderArgsListener)listener);
	    }
	}
    }

    public boolean avisynth() {
	return false;
    }

    public boolean excludeFormat(Format extension) {
	return false;
    }

    public boolean isPlayerCompatible(RendererConfiguration renderer) {
	return true;
    }

    public boolean isInternalSubtitlesSupported() {
	return true;
    }

    public boolean isExternalSubtitlesSupported() {
	return true;
    }

    public boolean isTimeSeekable() {
	return false;
    }

    public ProcessWrapper launchTranscode(
	String filename,
	DLNAResource dlna,
	DLNAMediaInfo media,
	OutputParams params)
	throws IOException
    {
	return launchTranscode(filename, media, params);
    }

    // XXX This (body instead of abstract) is a hack, but it saves us modifying all Players and preserves
    // backwards compatibility (which probably isn't needed)
    // TODO: revisit (i.e. deodorize) this for 1.30.x
    public ProcessWrapper launchTranscode(String filename, DLNAMediaInfo media, OutputParams params)
	throws IOException
    {
	throw new UnsupportedOperationException("The legacy launchTranscode() method must be overriden");
    }

    @Override
    public String toString() {
	return name();
    }

    protected String[] finalizeTranscoderArgs(
	String engine,
	String filename,
	DLNAResource dlna,
	DLNAMediaInfo media,
	OutputParams params,
	String[] cmdArgs)
    {
	if (finalizeTranscodeArgsListeners.isEmpty()) {
	    return cmdArgs;
	} else {
	    // make it mutable
	    List<String> cmdList = new ArrayList<String>(Arrays.asList(cmdArgs));

	    for (FinalizeTranscoderArgsListener listener : finalizeTranscodeArgsListeners) {
		cmdList = listener.finalizeTranscoderArgs(
		    engine,
		    filename,
		    dlna,
		    media,
		    params,
		    cmdList
		);
	    }

	    String[] cmdArray = new String[ cmdList.size() ];
	    cmdList.toArray(cmdArray);
	    return cmdArray;
	}
    }

    public void setAudioAndSubs(String fileName, DLNAMediaInfo media, OutputParams params, PmsConfiguration configuration) {
	if (params.aid == null && media != null) {
	    // check for preferred audio
	    StringTokenizer st = new StringTokenizer(configuration.getMencoderAudioLanguages(), ","); //$NON-NLS-1$
	    while (st != null && st.hasMoreTokens()) {
		String lang = st.nextToken();
		lang = lang.trim();
		PMS.debug("Looking for an audio track with lang: " + lang);
		for(DLNAMediaAudio audio:media.audioCodes) {
		    if (audio.matchCode(lang)) {
			params.aid = audio;
			PMS.debug("Matched audio track: " + audio);
			st = null;
			break;
		    }
		}
	    }
	}

	if (params.aid == null && media.audioCodes.size() > 0) {
	    // take a default audio track, dts first if possible
	    for (DLNAMediaAudio audio:media.audioCodes) {
		if (audio.isDTS()) {
		    params.aid = audio;
		    PMS.debug("Found priority audio track with DTS: " + audio);
		    break;
		}
	    }

	    if (params.aid == null) {
		params.aid = media.audioCodes.get(0);
		PMS.debug("Choosed a default audio track: " + params.aid);
	    }
	}

	String currentLang = null;
	DLNAMediaSubtitle matchedSub = null;

	if (params.aid != null)
	    currentLang = params.aid.lang;

	if (params.sid != null && params.sid.id == -1) {
	    PMS.debug("Don't want subtitles!");
	    params.sid = null;
	    return;
	}

	StringTokenizer st1 = new StringTokenizer(configuration.getMencoderAudioSubLanguages(), ";"); //$NON-NLS-1$

	while (st1.hasMoreTokens()) {
	    String pair = st1.nextToken();
	    if (pair.contains(",")) { //$NON-NLS-1$
		String audio = pair.substring(0, pair.indexOf(",")); //$NON-NLS-1$
		String sub = pair.substring(pair.indexOf(",")+1); //$NON-NLS-1$
		audio = audio.trim();
		sub = sub.trim();
		PMS.debug("Search a match for: " + currentLang + " with " + audio + " and " + sub);

		if (Iso639.isCodesMatching(audio, currentLang) || (currentLang != null && audio.equals("*"))) {
		    if (sub.equals("off")) {
			matchedSub = new DLNAMediaSubtitle();
			matchedSub.lang = "off";
		    } else {
			for(DLNAMediaSubtitle present_sub:media.subtitlesCodes) {
			    if (present_sub.matchCode(sub) || sub.equals("*")) {
				matchedSub = present_sub;
				PMS.debug(" Found a match: " + matchedSub);
				break;
			    }
			}
		    }

		    if (matchedSub != null)
			break;
		}
	    }
	}

	if (matchedSub != null && params.sid == null) {
	    if (matchedSub.lang != null && matchedSub.lang.equals("off")) {
		PMS.debug(" Disabled the subtitles: " + matchedSub);
		//return;
	    } else {
		params.sid = matchedSub;
	    }
	}

	if (!configuration.isMencoderDisableSubs() && params.sid == null && media != null) {
	    // check for subtitles again
	    File video = new File(fileName);
	    FileUtil.doesSubtitlesExists(video, media, false);

	    if (configuration.getUseSubtitles()) {
        		boolean forcedSubsFound = false;
		// priority to external subtitles
		for(DLNAMediaSubtitle sub:media.subtitlesCodes) {
			if (matchedSub !=null && matchedSub.lang !=null && matchedSub.lang.equals("off")) {
				StringTokenizer st = new StringTokenizer(configuration.getForcedSubsTags(), ","); //$NON-NLS-1$
				while (st != null && sub.flavor != null && st.hasMoreTokens()) {
					String forcedTags = st.nextToken();
					forcedTags = forcedTags.trim();
					if (sub.flavor.toLowerCase().indexOf(forcedTags) > -1) {
						if (Iso639.isCodesMatching(sub.lang,configuration.getMencoderSubLanguages())) {
							PMS.debug("Forcing preferred subtitles : " + sub.getLang() + "/" + sub.flavor);
							PMS.debug("Forced subtitles track : " + sub);
							if (sub.file != null) {
								PMS.debug("Found external forced file : " + sub.file.getAbsolutePath());
							}
							params.sid = sub;
							forcedSubsFound = true;
							break;
						}
					}
				}
				if (forcedSubsFound == true) break;
			} else {
				PMS.debug("Found subtitles track : " + sub);
				if (sub.file != null) {
					PMS.debug("Found external file : " + sub.file.getAbsolutePath());
					params.sid = sub;
					break;
				}
			}
		}
	}
      	if (matchedSub !=null && matchedSub.lang !=null && matchedSub.lang.equals("off")) return;

	    if (params.sid == null) {
		StringTokenizer st = new StringTokenizer(configuration.getMencoderSubLanguages(), ","); //$NON-NLS-1$
		while (st != null && st.hasMoreTokens()) {
		    String lang = st.nextToken();
		    lang = lang.trim();
		    PMS.debug("Looking for a subtitle track with lang: " + lang);
		    for(DLNAMediaSubtitle sub:media.subtitlesCodes) {
			if (sub.matchCode(lang)) {
			    params.sid = sub;
			    PMS.debug("Matched sub track: " + params.sid);
			    st = null;
			    break;
			}
		    }
		}
	    }
	}

    }
}
