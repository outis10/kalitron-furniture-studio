import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getDesignSessions } from 'app/entities/design-session/design-session.reducer';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { ProjectType } from 'app/shared/model/enumerations/project-type.model';
import { SiteMeasurementStatus } from 'app/shared/model/enumerations/site-measurement-status.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './site-measurement.reducer';

export const SiteMeasurementUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const designSessions = useAppSelector(state => state.designSession.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const siteMeasurementEntity = useAppSelector(state => state.siteMeasurement.entity);
  const loading = useAppSelector(state => state.siteMeasurement.loading);
  const updating = useAppSelector(state => state.siteMeasurement.updating);
  const updateSuccess = useAppSelector(state => state.siteMeasurement.updateSuccess);
  const projectTypeValues = Object.keys(ProjectType);
  const siteMeasurementStatusValues = Object.keys(SiteMeasurementStatus);

  const handleClose = () => {
    navigate(`/site-measurement${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getDesignSessions({}));
    dispatch(getUsers({}));
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
    if (values.revision !== undefined && typeof values.revision !== 'number') {
      values.revision = Number(values.revision);
    }
    if (values.schemaVersion !== undefined && typeof values.schemaVersion !== 'number') {
      values.schemaVersion = Number(values.schemaVersion);
    }
    if (values.floorOutOfLevelMm !== undefined && typeof values.floorOutOfLevelMm !== 'number') {
      values.floorOutOfLevelMm = Number(values.floorOutOfLevelMm);
    }
    values.capturedAt = convertDateTimeToServer(values.capturedAt);
    values.receivedAt = convertDateTimeToServer(values.receivedAt);
    values.confirmedAt = convertDateTimeToServer(values.confirmedAt);

    const entity = {
      ...siteMeasurementEntity,
      ...values,
      session: designSessions.find(it => it.id.toString() === values.session?.toString()),
      measuredBy: users.find(it => it.id.toString() === values.measuredBy?.toString()),
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
          capturedAt: displayDefaultDateTime(),
          receivedAt: displayDefaultDateTime(),
          confirmedAt: displayDefaultDateTime(),
        }
      : {
          projectType: 'KITCHEN',
          status: 'DRAFT',
          ...siteMeasurementEntity,
          capturedAt: convertDateTimeFromServer(siteMeasurementEntity.capturedAt),
          receivedAt: convertDateTimeFromServer(siteMeasurementEntity.receivedAt),
          confirmedAt: convertDateTimeFromServer(siteMeasurementEntity.confirmedAt),
          session: siteMeasurementEntity?.session?.id,
          measuredBy: siteMeasurementEntity?.measuredBy?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.siteMeasurement.home.createOrEditLabel" data-cy="SiteMeasurementCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.siteMeasurement.home.createOrEditLabel">
              Create or edit a SiteMeasurement
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
                  id="site-measurement-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.measurementUuid')}
                id="site-measurement-measurementUuid"
                name="measurementUuid"
                data-cy="measurementUuid"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.projectType')}
                id="site-measurement-projectType"
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
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.revision')}
                id="site-measurement-revision"
                name="revision"
                data-cy="revision"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.status')}
                id="site-measurement-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {siteMeasurementStatusValues.map(siteMeasurementStatus => (
                  <option value={siteMeasurementStatus} key={siteMeasurementStatus}>
                    {translate(`kalitronFurnitureStudioApp.SiteMeasurementStatus.${siteMeasurementStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.schemaVersion')}
                id="site-measurement-schemaVersion"
                name="schemaVersion"
                data-cy="schemaVersion"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.catalogVersion')}
                id="site-measurement-catalogVersion"
                name="catalogVersion"
                data-cy="catalogVersion"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.payload')}
                id="site-measurement-payload"
                name="payload"
                data-cy="payload"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.payloadSha256')}
                id="site-measurement-payloadSha256"
                name="payloadSha256"
                data-cy="payloadSha256"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.floorOutOfLevelMm')}
                id="site-measurement-floorOutOfLevelMm"
                name="floorOutOfLevelMm"
                data-cy="floorOutOfLevelMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.floorOutOfLevelNote')}
                id="site-measurement-floorOutOfLevelNote"
                name="floorOutOfLevelNote"
                data-cy="floorOutOfLevelNote"
                type="text"
                validate={{
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.deviceId')}
                id="site-measurement-deviceId"
                name="deviceId"
                data-cy="deviceId"
                type="text"
                validate={{
                  maxLength: { value: 64, message: translate('entity.validation.maxlength', { max: 64 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.appVersion')}
                id="site-measurement-appVersion"
                name="appVersion"
                data-cy="appVersion"
                type="text"
                validate={{
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.laserModel')}
                id="site-measurement-laserModel"
                name="laserModel"
                data-cy="laserModel"
                type="text"
                validate={{
                  maxLength: { value: 40, message: translate('entity.validation.maxlength', { max: 40 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.capturedAt')}
                id="site-measurement-capturedAt"
                name="capturedAt"
                data-cy="capturedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.receivedAt')}
                id="site-measurement-receivedAt"
                name="receivedAt"
                data-cy="receivedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.confirmedAt')}
                id="site-measurement-confirmedAt"
                name="confirmedAt"
                data-cy="confirmedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="site-measurement-session"
                name="session"
                data-cy="session"
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.session')}
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
              <ValidatedField
                id="site-measurement-measuredBy"
                name="measuredBy"
                data-cy="measuredBy"
                label={translate('kalitronFurnitureStudioApp.siteMeasurement.measuredBy')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/site-measurement" replace variant="info">
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

export default SiteMeasurementUpdate;
