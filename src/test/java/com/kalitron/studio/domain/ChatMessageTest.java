package com.kalitron.studio.domain;

import static com.kalitron.studio.domain.ChatMessageTestSamples.*;
import static com.kalitron.studio.domain.DesignSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.kalitron.studio.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChatMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChatMessage.class);
        ChatMessage chatMessage1 = getChatMessageSample1();
        ChatMessage chatMessage2 = new ChatMessage();
        assertThat(chatMessage1).isNotEqualTo(chatMessage2);

        chatMessage2.setId(chatMessage1.getId());
        assertThat(chatMessage1).isEqualTo(chatMessage2);

        chatMessage2 = getChatMessageSample2();
        assertThat(chatMessage1).isNotEqualTo(chatMessage2);
    }

    @Test
    void sessionTest() {
        ChatMessage chatMessage = getChatMessageRandomSampleGenerator();
        DesignSession designSessionBack = getDesignSessionRandomSampleGenerator();

        chatMessage.setSession(designSessionBack);
        assertThat(chatMessage.getSession()).isEqualTo(designSessionBack);

        chatMessage.session(null);
        assertThat(chatMessage.getSession()).isNull();
    }
}
