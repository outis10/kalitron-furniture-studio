import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getDesignSessions } from 'app/entities/design-session/design-session.reducer';
import { RoomObstacleType } from 'app/shared/model/enumerations/room-obstacle-type.model';

import { createEntity, getEntity, reset, updateEntity } from './room-obstacle.reducer';

export const RoomObstacleUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const designSessions = useAppSelector(state => state.designSession.entities);
  const roomObstacleEntity = useAppSelector(state => state.roomObstacle.entity);
  const loading = useAppSelector(state => state.roomObstacle.loading);
  const updating = useAppSelector(state => state.roomObstacle.updating);
  const updateSuccess = useAppSelector(state => state.roomObstacle.updateSuccess);
  const roomObstacleTypeValues = Object.keys(RoomObstacleType);

  const handleClose = () => {
    navigate('/room-obstacle');
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
    if (values.xMm !== undefined && typeof values.xMm !== 'number') {
      values.xMm = Number(values.xMm);
    }
    if (values.yMm !== undefined && typeof values.yMm !== 'number') {
      values.yMm = Number(values.yMm);
    }
    if (values.zMm !== undefined && typeof values.zMm !== 'number') {
      values.zMm = Number(values.zMm);
    }
    if (values.widthMm !== undefined && typeof values.widthMm !== 'number') {
      values.widthMm = Number(values.widthMm);
    }
    if (values.heightMm !== undefined && typeof values.heightMm !== 'number') {
      values.heightMm = Number(values.heightMm);
    }
    if (values.depthMm !== undefined && typeof values.depthMm !== 'number') {
      values.depthMm = Number(values.depthMm);
    }

    const entity = {
      ...roomObstacleEntity,
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
      ? {}
      : {
          obstacleType: 'WINDOW',
          ...roomObstacleEntity,
          session: roomObstacleEntity?.session?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.roomObstacle.home.createOrEditLabel" data-cy="RoomObstacleCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.roomObstacle.home.createOrEditLabel">Create or edit a RoomObstacle</Translate>
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
                  id="room-obstacle-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomObstacle.obstacleType')}
                id="room-obstacle-obstacleType"
                name="obstacleType"
                data-cy="obstacleType"
                type="select"
              >
                {roomObstacleTypeValues.map(roomObstacleType => (
                  <option value={roomObstacleType} key={roomObstacleType}>
                    {translate(`kalitronFurnitureStudioApp.RoomObstacleType.${roomObstacleType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomObstacle.label')}
                id="room-obstacle-label"
                name="label"
                data-cy="label"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomObstacle.xMm')}
                id="room-obstacle-xMm"
                name="xMm"
                data-cy="xMm"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomObstacle.yMm')}
                id="room-obstacle-yMm"
                name="yMm"
                data-cy="yMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomObstacle.zMm')}
                id="room-obstacle-zMm"
                name="zMm"
                data-cy="zMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomObstacle.widthMm')}
                id="room-obstacle-widthMm"
                name="widthMm"
                data-cy="widthMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomObstacle.heightMm')}
                id="room-obstacle-heightMm"
                name="heightMm"
                data-cy="heightMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomObstacle.depthMm')}
                id="room-obstacle-depthMm"
                name="depthMm"
                data-cy="depthMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomObstacle.notes')}
                id="room-obstacle-notes"
                name="notes"
                data-cy="notes"
                type="text"
                validate={{
                  maxLength: { value: 300, message: translate('entity.validation.maxlength', { max: 300 }) },
                }}
              />
              <ValidatedField
                id="room-obstacle-session"
                name="session"
                data-cy="session"
                label={translate('kalitronFurnitureStudioApp.roomObstacle.session')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/room-obstacle" replace variant="info">
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

export default RoomObstacleUpdate;
