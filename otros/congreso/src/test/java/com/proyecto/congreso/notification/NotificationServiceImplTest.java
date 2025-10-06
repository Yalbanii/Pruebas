package com.proyecto.congreso.notification;
import com.proyecto.congreso.notification.model.Notification;
import com.proyecto.congreso.notification.repository.NotificationRepository;
import com.proyecto.congreso.notification.service.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    private static final String ID = "N-101";
    private static final Long PARTICIPANT_ID = 500L;
    private static final Long PASS_ID = 101L;
    private static final String EMAIL = "test@example.com";
    private static final String FULL_NAME = "Tester Name";
    private static final Notification.NotificationStatus PENDING = Notification.NotificationStatus.PENDING;
    private static final Notification.NotificationStatus SENT = Notification.NotificationStatus.SENT;
    private static final Notification.NotificationStatus FAILED = Notification.NotificationStatus.FAILED;
    private static final Notification.NotificationStatus RETRY = Notification.NotificationStatus.RETRY;

    @Mock
    private NotificationRepository notificationRepository;

    // Usamos Spy para poder mockear el método privado 'simulateSendNotification'
    @InjectMocks
    private NotificationServiceImpl notificationServiceSpy = Mockito.spy(new NotificationServiceImpl(notificationRepository));

    private Notification mockNotification;
    private List<Notification> mockNotificationList;

    @BeforeEach
    void setUp() throws Exception {
        mockNotification = new Notification();
        mockNotification.setId(ID);
        mockNotification.setParticipantId(PARTICIPANT_ID);
        mockNotification.setStatus(PENDING);
        mockNotification.setChannel(Notification.NotificationChannel.EMAIL);
        mockNotification.setType(Notification.NotificationType.EXCHANGE);
        mockNotification.setSubject("Test Subject");

        mockNotificationList = List.of(mockNotification);

        // Mockear el método privado para que simule éxito por defecto
        doReturn(true).when(notificationServiceSpy).simulateSendNotification(any(Notification.class));
    }

    @Test
    void sendPendingNotifications_shouldCallSendNotificationForEachPending() {
        Notification pending1 = new Notification(); pending1.setId("P1");
        Notification pending2 = new Notification(); pending2.setId("P2");
        List<Notification> pendingList = List.of(pending1, pending2);

        doReturn(pendingList).when(notificationServiceSpy).getNotificationsByStatus(PENDING);
        doNothing().when(notificationServiceSpy).sendNotification(anyString());

        notificationServiceSpy.sendPendingNotifications();

        verify(notificationServiceSpy).getNotificationsByStatus(PENDING);
        verify(notificationServiceSpy, times(2)).sendNotification(anyString());
    }

}