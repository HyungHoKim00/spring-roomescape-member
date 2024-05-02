package roomescape.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixture.ROOM_THEME_FIXTURE;

import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import roomescape.domain.RoomTheme;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomThemeDaoTest {
    @LocalServerPort
    private int port;

    @Autowired
    private RoomThemeDao roomThemeDao;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        List<RoomTheme> roomThemes = roomThemeDao.findAll();
        for (RoomTheme roomTheme : roomThemes) {
            roomThemeDao.deleteById(roomTheme.getId());
        }
    }

    @DisplayName("테마를 저장한다.")
    @Test
    void save() {
        // given
        RoomTheme roomTheme = ROOM_THEME_FIXTURE;
        // when
        RoomTheme savedRoomTheme = roomThemeDao.save(roomTheme);
        // then
        assertAll(
                () -> assertThat(savedRoomTheme.getName()).isEqualTo(roomTheme.getName()),
                () -> assertThat(savedRoomTheme.getDescription()).isEqualTo(
                        roomTheme.getDescription()),
                () -> assertThat(savedRoomTheme.getThumbnail()).isEqualTo(roomTheme.getThumbnail())
        );
    }

    @DisplayName("저장된 모든 테마를 보여준다.")
    @Test
    void findAll() {
        // given & when
        List<RoomTheme> roomThemes = roomThemeDao.findAll();
        // then
        assertThat(roomThemes).isEmpty();
    }

    @DisplayName("테마를 삭제한다.")
    @Test
    void deleteTheme() {
        // given
        RoomTheme roomTheme = ROOM_THEME_FIXTURE;
        RoomTheme savedRoomTheme = roomThemeDao.save(roomTheme);
        // when
        roomThemeDao.deleteById(savedRoomTheme.getId());
        // then
        assertThat(roomThemeDao.findAll()).isEmpty();
    }

    @DisplayName("해당 id의 테마를 보여준다.")
    @Test
    void findById() {
        // given
        RoomTheme roomTheme = ROOM_THEME_FIXTURE;
        RoomTheme savedRoomTheme = roomThemeDao.save(roomTheme);
        // when
        RoomTheme findRoomTheme = roomThemeDao.findById(savedRoomTheme.getId());
        // then
        assertAll(
                () -> assertThat(findRoomTheme.getId()).isEqualTo(savedRoomTheme.getId()),
                () -> assertThat(findRoomTheme.getName()).isEqualTo(savedRoomTheme.getName()),
                () -> assertThat(findRoomTheme.getDescription()).isEqualTo(
                        savedRoomTheme.getDescription()),
                () -> assertThat(findRoomTheme.getThumbnail()).isEqualTo(
                        savedRoomTheme.getThumbnail())
        );
    }
}