package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.model.ReadOnlySchedule;


/**
 * A class to access Schedule data stored as an xml file on the hard disk.
 */
public class XmlScheduleStorage implements ScheduleStorage {

    private static final Logger logger = LogsCenter.getLogger(XmlScheduleStorage.class);

    private Path filePath;

    public XmlScheduleStorage(Path filePath) { this.filePath = filePath; }

    public Path getScheduleFilePath() { return filePath; }

    @Override
    public Optional<ReadOnlySchedule> readSchedule() throws DataConversionException, IOException, ParseException {
        return readSchedule(filePath);
    }

    /**
     * Similar to {@link #readSchedule()}
     * @param filePath location of the data. Cannot be null.
     * @throws DataConversionException if the file is not in the correct format.
     */
    public Optional<ReadOnlySchedule> readSchedule(Path filePath) throws DataConversionException,
                                                                            FileNotFoundException,
                                                                            ParseException {
        requireNonNull(filePath);

        if (!Files.exists(filePath)) {
            logger.info("Schedule file " + filePath + " not found");
            return Optional.empty();
        }

        XmlSerializableSchedule xmlSchedule = XmlFileStorage.loadScheduleFromSaveFile(filePath);
        try {
            return Optional.of(xmlSchedule.toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataConversionException(ive);
        }
    }

    @Override
    public void saveSchedule(ReadOnlySchedule schedule) throws IOException {
        saveSchedule(schedule, filePath);
    }

    /**
     * saves schedule to file
     * @param filePath location of the data. Cannot be null
     */
    public void saveSchedule(ReadOnlySchedule schedule, Path filePath) throws IOException {
        requireNonNull(schedule);
        requireNonNull(filePath);
        System.out.println("file path:");
        System.out.println(filePath);

        FileUtil.createIfMissing(filePath);
        XmlFileStorage.saveScheduleToFile(filePath, new XmlSerializableSchedule(schedule));
    }
}
