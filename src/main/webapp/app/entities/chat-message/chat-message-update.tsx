import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getDesignSessions } from 'app/entities/design-session/design-session.reducer';
import { MessageRole } from 'app/shared/model/enumerations/message-role.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './chat-message.reducer';

export const ChatMessageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const designSessions = useAppSelector(state => state.designSession.entities);
  const chatMessageEntity = useAppSelector(state => state.chatMessage.entity);
  const loading = useAppSelector(state => state.chatMessage.loading);
  const updating = useAppSelector(state => state.chatMessage.updating);
  const updateSuccess = useAppSelector(state => state.chatMessage.updateSuccess);
  const messageRoleValues = Object.keys(MessageRole);

  const handleClose = () => {
    navigate(`/chat-message${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getDesignSessions({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.tokenCount !== undefined && typeof values.tokenCount !== 'number') {
      values.tokenCount = Number(values.tokenCount);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...chatMessageEntity,
      ...values,
      session: designSessions.find(it => it.id.toString() === values.session?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdAt: displayDefaultDateTime(),
        }
      : {
          role: 'USER',
          ...chatMessageEntity,
          createdAt: convertDateTimeFromServer(chatMessageEntity.createdAt),
          session: chatMessageEntity?.session?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.chatMessage.home.createOrEditLabel" data-cy="ChatMessageCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.chatMessage.home.createOrEditLabel">Create or edit a ChatMessage</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="chat-message-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.chatMessage.role')}
                id="chat-message-role"
                name="role"
                data-cy="role"
                type="select"
              >
                {messageRoleValues.map(messageRole => (
                  <option value={messageRole} key={messageRole}>
                    {translate(`kalitronFurnitureStudioApp.MessageRole.${messageRole}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.chatMessage.content')}
                id="chat-message-content"
                name="content"
                data-cy="content"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.chatMessage.tokenCount')}
                id="chat-message-tokenCount"
                name="tokenCount"
                data-cy="tokenCount"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.chatMessage.createdAt')}
                id="chat-message-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="chat-message-session"
                name="session"
                data-cy="session"
                label={translate('kalitronFurnitureStudioApp.chatMessage.session')}
                type="select"
                required
              >
                <option value="" key="0" />
                {designSessions
                  ? designSessions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.sessionCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/chat-message" replace variant="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button variant="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ChatMessageUpdate;
