package org.nentangso.core.service.utils;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Text Utils Unit Tests power by nentangso.org")
public class NtsTextUtilsTests {
    @Test
    @DisplayName("Unaccent Vietnamese")
    public void unaccentVietnamese() {
        assertEquals("a A a A a A a A a A a A", NtsTextUtils.unaccentVietnamese("a A á Á à À ả Ả ã Ã ạ Ạ"));
        assertEquals("a A a A a A a A a A a A", NtsTextUtils.unaccentVietnamese("ă Ă ắ Ắ ằ Ằ ẳ Ẳ ẵ Ẵ ặ Ặ"));
        assertEquals("a A a A a A a A a A a A", NtsTextUtils.unaccentVietnamese("â Â ấ Ấ ầ Ầ ẩ Ẩ ẫ Ẫ ậ Ậ"));
        assertEquals("d D d D", NtsTextUtils.unaccentVietnamese("d D đ Đ"));
        assertEquals("e E e E e E e E e E e E", NtsTextUtils.unaccentVietnamese("e E é É è È ẻ Ẻ ẽ Ẽ ẹ Ẹ"));
        assertEquals("e E e E e E e E e E e E", NtsTextUtils.unaccentVietnamese("ê Ê ế Ế ề Ề ể Ể ễ Ễ ệ Ệ"));
        assertEquals("i I i I i I i I i I i I", NtsTextUtils.unaccentVietnamese("i I í Í ì Ì ỉ Ỉ ĩ Ĩ ị Ị"));
        assertEquals("o O o O o O o O o O o O", NtsTextUtils.unaccentVietnamese("o O ó Ó ò Ò ỏ Ỏ õ Õ ọ Ọ"));
        assertEquals("o O o O o O o O o O o O", NtsTextUtils.unaccentVietnamese("ô Ô ố Ố ồ Ồ ổ Ổ ỗ Ỗ ộ Ộ"));
        assertEquals("o O o O o O o O o O o O", NtsTextUtils.unaccentVietnamese("ơ Ơ ớ Ớ ờ Ờ ở Ở ỡ Ỡ ợ Ợ"));
        assertEquals("u U u U u U u U u U u U", NtsTextUtils.unaccentVietnamese("u U ú Ú ù Ù ủ Ủ ũ Ũ ụ Ụ"));
        assertEquals("u U u U u U u U u U u U", NtsTextUtils.unaccentVietnamese("ư Ư ứ Ứ ừ Ừ ử Ử ữ Ữ ự Ự"));
        assertEquals("y Y y Y y Y y Y y Y y Y", NtsTextUtils.unaccentVietnamese("y Y ý Ý ỳ Ỳ ỷ Ỷ ỹ Ỹ ỵ Ỵ"));
        assertEquals("John Doe <join@example.com>", NtsTextUtils.unaccentVietnamese("John Doe <join@example.com>"));
        assertEquals("Nguyen Van Long <longnguyen@example.com>", NtsTextUtils.unaccentVietnamese("Nguyễn Văn Long <longnguyen@example.com>"));
        assertEquals("NGUYEN VAN LONG", NtsTextUtils.unaccentVietnamese("NGUYỄN VĂN LONG"));
        assertNull(NtsTextUtils.unaccentVietnamese(null));
        assertEquals("", NtsTextUtils.unaccentVietnamese(""));
        assertEquals(" ", NtsTextUtils.unaccentVietnamese(" "));
    }
}
