package messageUtils;

import messageUtils.SharedConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SharedConstantsTest {

    @Test
    public void testConstantValues() {
        assertEquals(0, SharedConstants.COMMTEST_CODE);
        assertEquals(1, SharedConstants.DISCONN_CODE);
        assertEquals(2, SharedConstants.ACK_CODE);
        assertEquals(3, SharedConstants.ERR_CODE);
        assertEquals(4, SharedConstants.AUTH_CODE);
        assertEquals(5, SharedConstants.OWNED_BOARDS_REQUEST_CODE);
        assertEquals(6, SharedConstants.OWNED_BOARDS_RESPONSE_CODE);
        assertEquals(7, SharedConstants.SHARE_BOARD_REQUEST_CODE);
        assertEquals(8, SharedConstants.SHARE_BOARD_RESPONSE_CODE);
        assertEquals(9, SharedConstants.FIND_OWNED_BOARDS_CODE);
        assertEquals(10, SharedConstants.ARCHIVE_BOARD_CODE);
        assertEquals(11, SharedConstants.CREATE_POST_IT_REQUEST_CODE);
        assertEquals(12, SharedConstants.CREATE_POST_IT_RESPONSE_CODE);
        assertEquals(13, SharedConstants.UPDATE_POST_IT_TEXT_REQUEST_CODE);
        assertEquals(14, SharedConstants.UPDATE_POST_IT_URL_REQUEST_CODE);
        assertEquals(15, SharedConstants.UPDATE_POST_IT_CELL_REQUEST_CODE);
        assertEquals(16, SharedConstants.REMOVE_POST_IT_REQUEST_CODE);
        assertEquals(17, SharedConstants.UPDATE_POST_IT_RESPONSE_CODE);
        assertEquals(17, SharedConstants.CURRENT_MAX_CODE);
        assertEquals(1, SharedConstants.MESSAGE_VERSION);
        assertEquals(9999, SharedConstants.DEFAULT_PORT);
        assertEquals(5, SharedConstants.TIMEOUT);
    }
}
