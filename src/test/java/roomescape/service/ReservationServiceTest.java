package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixture.DATE_FIXTURE;
import static roomescape.TestFixture.RESERVATION_TIME_FIXTURE;
import static roomescape.TestFixture.ROOM_THEME_FIXTURE;
import static roomescape.TestFixture.TIME_FIXTURE;

import io.restassured.RestAssured;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import roomescape.dao.ReservationDao;
import roomescape.dao.ReservationTimeDao;
import roomescape.dao.RoomThemeDao;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.RoomTheme;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.exception.InvalidInputException;
import roomescape.exception.TargetNotExistException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationServiceTest {
    @LocalServerPort
    private int port;

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationDao reservationDao;
    @Autowired
    private ReservationTimeDao reservationTimeDao;
    @Autowired
    private RoomThemeDao roomThemeDao;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        List<Reservation> reservations = reservationDao.findAll();
        for (Reservation reservation : reservations) {
            reservationDao.deleteById(reservation.getId());
        }
        List<ReservationTime> reservationTimes = reservationTimeDao.findAll();
        for (ReservationTime reservationTime : reservationTimes) {
            reservationTimeDao.deleteById(reservationTime.getId());
        }
        List<RoomTheme> roomThemes = roomThemeDao.findAll();
        for (RoomTheme roomTheme : roomThemes) {
            roomThemeDao.deleteById(roomTheme.getId());
        }
    }

    @DisplayName("모든 예약 검색")
    @Test
    void findAll() {
        assertThat(reservationService.findAll()).isEmpty();
    }

    @DisplayName("예약 저장")
    @Test
    void save() {
        // given
        ReservationRequest reservationRequest = createReservationRequest(DATE_FIXTURE);
        // when
        ReservationResponse response = reservationService.save(reservationRequest);
        // then
        assertAll(
                () -> assertThat(reservationService.findAll()).hasSize(1),
                () -> assertThat(response.name()).isEqualTo("aa"),
                () -> assertThat(response.date()).isEqualTo(DATE_FIXTURE),
                () -> assertThat(response.time().startAt()).isEqualTo(TIME_FIXTURE)
        );
    }

    @DisplayName("지난 예약을 저장하려 하면 예외가 발생한다.")
    @Test
    void pastReservationSave() {
        // given
        ReservationRequest reservationRequest = createReservationRequest(LocalDate.of(2000, 11, 9));
        // when & then
        assertThatThrownBy(() -> reservationService.save(reservationRequest))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("지난 날짜에는 예약할 수 없습니다.");
    }

    @DisplayName("중복 예약을 저장하려 하면 예외가 발생한다.")
    @Test
    void duplicatedReservationSave() {
        // given
        ReservationRequest reservationRequest = createReservationRequest(DATE_FIXTURE);
        reservationService.save(reservationRequest);
        // when & then
        assertThatThrownBy(() -> reservationService.save(reservationRequest))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("예약이 이미 존재합니다.");
    }

    @DisplayName("삭제 테스트")
    @Test
    void deleteById() {
        // given
        ReservationRequest request = createReservationRequest(DATE_FIXTURE);
        ReservationResponse response = reservationService.save(request);
        // when
        reservationService.deleteById(response.id());
        // then
        assertThat(reservationService.findAll()).isEmpty();
    }

    @DisplayName("존재하지 않는 id의 대상을 삭제하려 하면 예외가 발생한다.")
    @Test
    void deleteByNotExistingId() {
        assertThatThrownBy(() -> reservationService.deleteById(-1L))
                .isInstanceOf(TargetNotExistException.class)
                .hasMessage("삭제할 예약이 존재하지 않습니다.");
    }

    private ReservationRequest createReservationRequest(LocalDate date) {
        ReservationTime savedReservationTime = reservationTimeDao.save(
                RESERVATION_TIME_FIXTURE);
        RoomTheme savedRoomTheme = roomThemeDao.save(ROOM_THEME_FIXTURE);
        return new ReservationRequest("aa", date,
                savedReservationTime.getId(), savedRoomTheme.getId());
    }
}
