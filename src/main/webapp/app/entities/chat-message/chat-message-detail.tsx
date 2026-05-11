import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './chat-message.reducer';

export const ChatMessageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const chatMessageEntity = useAppSelector(state => state.chatMessage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="chatMessageDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.chatMessage.detail.title">ChatMessage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{chatMessageEntity.id}</dd>
          <dt>
            <span id="role">
              <Translate contentKey="kalitronFurnitureStudioApp.chatMessage.role">Role</Translate>
            </span>
          </dt>
          <dd>{chatMessageEntity.role}</dd>
          <dt>
            <span id="content">
              <Translate contentKey="kalitronFurnitureStudioApp.chatMessage.content">Content</Translate>
            </span>
          </dt>
          <dd>{chatMessageEntity.content}</dd>
          <dt>
            <span id="tokenCount">
              <Translate contentKey="kalitronFurnitureStudioApp.chatMessage.tokenCount">Token Count</Translate>
            </span>
          </dt>
          <dd>{chatMessageEntity.tokenCount}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="kalitronFurnitureStudioApp.chatMessage.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {chatMessageEntity.createdAt ? <TextFormat value={chatMessageEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.chatMessage.session">Session</Translate>
          </dt>
          <dd>{chatMessageEntity.session ? chatMessageEntity.session.sessionCode : ''}</dd>
        </dl>
        <Button as={Link as any} to="/chat-message" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/chat-message/${chatMessageEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ChatMessageDetail;
