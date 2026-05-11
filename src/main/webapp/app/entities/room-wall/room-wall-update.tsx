import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getDesignSessions } from 'app/entities/design-session/design-session.reducer';

import { createEntity, getEntity, reset, updateEntity } from './room-wall.reducer';

export const RoomWallUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const designSessions = useAppSelector(state => state.designSession.entities);
  const roomWallEntity = useAppSelector(state => state.roomWall.entity);
  const loading = useAppSelector(state => state.roomWall.loading);
  const updating = useAppSelector(state => state.roomWall.updating);
  const updateSuccess = useAppSelector(state => state.roomWall.updateSuccess);

  const handleClose = () => {
    navigate('/room-wall');
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
    if (values.lengthMm !== undefined && typeof values.lengthMm !== 'number') {
      values.lengthMm = Number(values.lengthMm);
    }
    if (values.heightMm !== undefined && typeof values.heightMm !== 'number') {
      values.heightMm = Number(values.heightMm);
    }
    if (values.angleDeg !== undefined && typeof values.angleDeg !== 'number') {
      values.angleDeg = Number(values.angleDeg);
    }
    if (values.positionX !== undefined && typeof values.positionX !== 'number') {
      values.positionX = Number(values.positionX);
    }
    if (values.positionY !== undefined && typeof values.positionY !== 'number') {
      values.positionY = Number(values.positionY);
    }
    if (values.sortOrder !== undefined && typeof values.sortOrder !== 'number') {
      values.sortOrder = Number(values.sortOrder);
    }

    const entity = {
      ...roomWallEntity,
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
          ...roomWallEntity,
          session: roomWallEntity?.session?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.roomWall.home.createOrEditLabel" data-cy="RoomWallCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.roomWall.home.createOrEditLabel">Create or edit a RoomWall</Translate>
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
                  id="room-wall-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomWall.name')}
                id="room-wall-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomWall.lengthMm')}
                id="room-wall-lengthMm"
                name="lengthMm"
                data-cy="lengthMm"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomWall.heightMm')}
                id="room-wall-heightMm"
                name="heightMm"
                data-cy="heightMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomWall.angleDeg')}
                id="room-wall-angleDeg"
                name="angleDeg"
                data-cy="angleDeg"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomWall.positionX')}
                id="room-wall-positionX"
                name="positionX"
                data-cy="positionX"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomWall.positionY')}
                id="room-wall-positionY"
                name="positionY"
                data-cy="positionY"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.roomWall.sortOrder')}
                id="room-wall-sortOrder"
                name="sortOrder"
                data-cy="sortOrder"
                type="text"
              />
              <ValidatedField
                id="room-wall-session"
                name="session"
                data-cy="session"
                label={translate('kalitronFurnitureStudioApp.roomWall.session')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/room-wall" replace variant="info">
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

export default RoomWallUpdate;
