package com.project.knowledgeassistant.services;

import com.project.knowledgeassistant.DTOs.DocumentChunkData;
import com.project.knowledgeassistant.DTOs.ExtractedPage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;



@Service
public class ChunkingService {

    private static final int CHUNK_SIZE = 1000;
    private static final int OVERLAP_SIZE = 200;



    public List<DocumentChunkData> createChunksWithOverlap(
            String text ) {

        List<DocumentChunkData> chunks =
                new ArrayList<>();

        int globalChunkNumber = 0;

        int start = 0;

        while (start < text.length()) {

            int end = Math.min(
                    start + CHUNK_SIZE,
                    text.length()
            );

            String chunk =
                    text.substring(start, end).trim();

            if (!chunk.isEmpty()) {

                chunks.add(
                        new DocumentChunkData(
                                chunk,
                                globalChunkNumber++
                        )
                );
            }

            start +=
                    CHUNK_SIZE - OVERLAP_SIZE;
        }

        return chunks;
    }


}