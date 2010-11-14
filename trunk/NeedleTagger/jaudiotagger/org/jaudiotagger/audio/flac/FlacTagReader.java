/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Raphaël Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jaudiotagger.audio.flac;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture;
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockHeader;
import org.jaudiotagger.tag.InvalidFrameException;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentReader;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Read Flac Tag
 */
public class FlacTagReader
{
    // Logger Object
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio.flac");

    private VorbisCommentReader vorbisCommentReader = new VorbisCommentReader();


    public FlacTag read(RandomAccessFile raf) throws CannotReadException, IOException
    {
        FlacStreamReader flacStream = new FlacStreamReader(raf);
        flacStream.findStream();

        //Hold the metadata
        VorbisCommentTag tag = null;
        List<MetadataBlockDataPicture> images = new ArrayList<MetadataBlockDataPicture>();

        //Seems like we have a valid stream
        boolean isLastBlock = false;
        while (!isLastBlock)
        {
            if(logger.isLoggable(Level.INFO))
            {
                logger.info("Looking for MetaBlockHeader at:"+raf.getFilePointer());
            }
            
            //Read the header
            MetadataBlockHeader mbh = MetadataBlockHeader.readHeader(raf);

            if(logger.isLoggable(Level.INFO))
            {
                logger.info("Reading MetadataBlockHeader:"+mbh.toString() + " ending at "+raf.getFilePointer());
            }
            
            //Is it one containing some sort of metadata, therefore interested in it?
            switch (mbh.getBlockType())
            {
                //We got a vorbiscomment comment block, parse it
                case VORBIS_COMMENT:
                    byte[] commentHeaderRawPacket = new byte[mbh.getDataLength()];
                    raf.read(commentHeaderRawPacket);
                    tag = vorbisCommentReader.read(commentHeaderRawPacket, false);
                    break;

                case PICTURE:
                    try
                    {
                        MetadataBlockDataPicture mbdp = new MetadataBlockDataPicture(mbh, raf);
                        images.add(mbdp);
                    }
                    catch (IOException ioe)
                    {
                        logger.warning("Unable to read picture metablock, ignoring:" + ioe.getMessage());
                    }
                    catch (InvalidFrameException ive)
                    {
                        logger.warning("Unable to read picture metablock, ignoring" + ive.getMessage());
                    }

                    break;

                //This is not a metadata block we are interested in so we skip to next block
                default:
                    if(logger.isLoggable(Level.INFO))
                    {
                        logger.info("Ignoring MetadataBlock:"+mbh.getBlockType());
                    }
                    raf.seek(raf.getFilePointer() + mbh.getDataLength());
                    break;
            }

            isLastBlock = mbh.isLastBlock();
            mbh = null;
        }

        //Note there may not be either a tag or any images, no problem this is valid however to make it easier we
        //just initialize Flac with an empty VorbisTag
        if (tag == null)
        {
            tag = VorbisCommentTag.createNewTag();
        }
        FlacTag flacTag = new FlacTag(tag, images);
        return flacTag;
    }
}

