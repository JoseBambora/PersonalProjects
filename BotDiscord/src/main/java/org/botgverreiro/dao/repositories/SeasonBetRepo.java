package org.botgverreiro.dao.repositories;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import org.botgverreiro.dao.utils.EnvDB;
import org.botgverreiro.model.classes.SeasonBet;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SeasonBetRepo {
    public static int writeBets(List<SeasonBet> seasonBetList) {
        ObjectWriter writer = SeasonBet.getWriterCSV();
        try {
            SequenceWriter sequenceWriter = writer.writeValues(new File(EnvDB.file_season_bets));
            sequenceWriter.writeAll(seasonBetList);
            sequenceWriter.close();
            return 0;
        } catch (IOException ignored) {
            return 1;
        }
    }

    public static List<SeasonBet> readBets() {
        ObjectReader reader = SeasonBet.getReaderCSV();
        try {
            MappingIterator<SeasonBet> mappingIterator = reader.readValues(new File(EnvDB.file_season_bets));
            List<SeasonBet> res = mappingIterator.readAll();
            mappingIterator.close();
            return res;
        } catch (IOException ignored) {
            return Collections.emptyList();
        }
    }
}
