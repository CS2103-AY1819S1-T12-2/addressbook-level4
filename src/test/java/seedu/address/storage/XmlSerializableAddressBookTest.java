package seedu.address.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.testutil.PersonUtil.matchProperties;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.collections.ObservableList;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.XmlUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.testutil.TypicalPersons;

public class XmlSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data",
            "XmlSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.xml");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.xml");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.xml");

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        XmlSerializableAddressBook dataFromFile = XmlUtil.getDataFromFile(TYPICAL_PERSONS_FILE,
                XmlSerializableAddressBook.class);
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();

        ObservableList<Person> personList = addressBookFromFile.getPersonList();
        personList.sort((a, b) -> a.getName().toString().compareTo(b.getName().toString()));
        ObservableList<Person> sampleList = typicalPersonsAddressBook.getPersonList();
        sampleList.sort((a, b) -> a.getName().toString().compareTo(b.getName().toString()));

        assertEquals(personList.size(), sampleList.size());
        // we have to match properties since they will have different IDs
        for (int i = 0; i < personList.size(); ++i) {
            assertTrue(matchProperties(personList.get(i), sampleList.get(i)));
        }
    }


    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        XmlSerializableAddressBook dataFromFile = XmlUtil.getDataFromFile(INVALID_PERSON_FILE,
                XmlSerializableAddressBook.class);
        thrown.expect(IllegalValueException.class);
        dataFromFile.toModelType();
    }


    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        XmlSerializableAddressBook dataFromFile = XmlUtil.getDataFromFile(DUPLICATE_PERSON_FILE,
                XmlSerializableAddressBook.class);
        thrown.expect(IllegalValueException.class);
        thrown.expectMessage(XmlSerializableAddressBook.MESSAGE_DUPLICATE_PERSON);
        dataFromFile.toModelType();
    }


}
