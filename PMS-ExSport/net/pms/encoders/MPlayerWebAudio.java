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

import java.io.IOException;

import javax.swing.JComponent;

import net.pms.configuration.PmsConfiguration;
import net.pms.dlna.DLNAMediaInfo;
import net.pms.io.OutputParams;
import net.pms.io.ProcessWrapper;

public class MPlayerWebAudio extends MPlayerAudio {
	
	public static final String ID = "mplayerwebaudio"; //$NON-NLS-1$

	public MPlayerWebAudio(PmsConfiguration configuration) {
		super(configuration);
	}

	@Override
	public JComponent config() {
		return null;
	}
	
	@Override
	public int purpose() {
		return AUDIO_WEBSTREAM_PLAYER;
	}
	
	@Override
	public String id() {
		return "mplayerwebaudio"; //$NON-NLS-1$
	}
	
	@Override
	public ProcessWrapper launchTranscode(String fileName, DLNAMediaInfo media,
			OutputParams params) throws IOException {
		params.minBufferSize = params.minFileSize;
		params.secondread_minsize = 100000;
		params.waitbeforestart = 8000;
		return super.launchTranscode(fileName, media, params);
	}

	@Override
	public String name() {
		return "MPlayer Web"; //$NON-NLS-1$
	}
	
	@Override
	public boolean isTimeSeekable() {
		return false;
	}
}
