import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getCatalogStyles } from 'app/entities/catalog-style/catalog-style.reducer';
import { getEntities as getKitchenSpecs } from 'app/entities/kitchen-spec/kitchen-spec.reducer';
import { ProjectType } from 'app/shared/model/enumerations/project-type.model';
import { SessionStatus } from 'app/shared/model/enumerations/session-status.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './design-session.reducer';

export const DesignSessionUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const kitchenSpecs = useAppSelector(state => state.kitchenSpec.entities);
  const catalogStyles = useAppSelector(state => state.catalogStyle.entities);
  const designSessionEntity = useAppSelector(state => state.designSession.entity);
  const loading = useAppSelector(state => state.designSession.loading);
  const updating = useAppSelector(state => state.designSession.updating);
  const updateSuccess = useAppSelector(state => state.designSession.updateSuccess);
  const projectTypeValues = Object.keys(ProjectType);
  const sessionStatusValues = Object.keys(SessionStatus);

  const handleClose = () => {
    navigate(`/design-session${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getKitchenSpecs({}));
    dispatch(getCatalogStyles({}));
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
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...designSessionEntity,
      ...values,
      spec: kitchenSpecs.find(it => it.id.toString() === values.spec?.toString()),
      catalogStyle: catalogStyles.find(it => it.id.toString() === values.catalogStyle?.toString()),
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
          updatedAt: displayDefaultDateTime(),
        }
      : {
          projectType: 'KITCHEN',
          status: 'DRAFT',
          ...designSessionEntity,
          createdAt: convertDateTimeFromServer(designSessionEntity.createdAt),
          updatedAt: convertDateTimeFromServer(designSessionEntity.updatedAt),
          spec: designSessionEntity?.spec?.id,
          catalogStyle: designSessionEntity?.catalogStyle?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.designSession.home.createOrEditLabel" data-cy="DesignSessionCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.designSession.home.createOrEditLabel">
              Create or edit a DesignSession
            </Translate>
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
                  id="design-session-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designSession.sessionCode')}
                id="design-session-sessionCode"
                name="sessionCode"
                data-cy="sessionCode"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designSession.projectType')}
                id="design-session-projectType"
                name="projectType"
                data-cy="projectType"
                type="select"
              >
                {projectTypeValues.map(projectType => (
                  <option value={projectType} key={projectType}>
                    {translate(`kalitronFurnitureStudioApp.ProjectType.${projectType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designSession.status')}
                id="design-session-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {sessionStatusValues.map(sessionStatus => (
                  <option value={sessionStatus} key={sessionStatus}>
                    {translate(`kalitronFurnitureStudioApp.SessionStatus.${sessionStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designSession.clientName')}
                id="design-session-clientName"
                name="clientName"
                data-cy="clientName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 120, message: translate('entity.validation.maxlength', { max: 120 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designSession.clientEmail')}
                id="design-session-clientEmail"
                name="clientEmail"
                data-cy="clientEmail"
                type="text"
                validate={{
                  maxLength: { value: 120, message: translate('entity.validation.maxlength', { max: 120 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designSession.clientPhone')}
                id="design-session-clientPhone"
                name="clientPhone"
                data-cy="clientPhone"
                type="text"
                validate={{
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designSession.selectedStyle')}
                id="design-session-selectedStyle"
                name="selectedStyle"
                data-cy="selectedStyle"
                type="text"
                validate={{
                  maxLength: { value: 80, message: translate('entity.validation.maxlength', { max: 80 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designSession.notes')}
                id="design-session-notes"
                name="notes"
                data-cy="notes"
                type="text"
                validate={{
                  maxLength: { value: 2000, message: translate('entity.validation.maxlength', { max: 2000 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designSession.createdAt')}
                id="design-session-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designSession.updatedAt')}
                id="design-session-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="design-session-spec"
                name="spec"
                data-cy="spec"
                label={translate('kalitronFurnitureStudioApp.designSession.spec')}
                type="select"
              >
                <option value="" key="0" />
                {kitchenSpecs
                  ? kitchenSpecs.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.style}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="design-session-catalogStyle"
                name="catalogStyle"
                data-cy="catalogStyle"
                label={translate('kalitronFurnitureStudioApp.designSession.catalogStyle')}
                type="select"
              >
                <option value="" key="0" />
                {catalogStyles
                  ? catalogStyles.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/design-session" replace variant="info">
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

export default DesignSessionUpdate;
