import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getDesignArtifacts } from 'app/entities/design-artifact/design-artifact.reducer';
import { getEntities as getDesignSessions } from 'app/entities/design-session/design-session.reducer';
import { GenerationJobStatus } from 'app/shared/model/enumerations/generation-job-status.model';
import { GenerationJobType } from 'app/shared/model/enumerations/generation-job-type.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './generation-job.reducer';

export const GenerationJobUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const designArtifacts = useAppSelector(state => state.designArtifact.entities);
  const designSessions = useAppSelector(state => state.designSession.entities);
  const generationJobEntity = useAppSelector(state => state.generationJob.entity);
  const loading = useAppSelector(state => state.generationJob.loading);
  const updating = useAppSelector(state => state.generationJob.updating);
  const updateSuccess = useAppSelector(state => state.generationJob.updateSuccess);
  const generationJobTypeValues = Object.keys(GenerationJobType);
  const generationJobStatusValues = Object.keys(GenerationJobStatus);

  const handleClose = () => {
    navigate(`/generation-job${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getDesignArtifacts({}));
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
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.startedAt = convertDateTimeToServer(values.startedAt);
    values.finishedAt = convertDateTimeToServer(values.finishedAt);

    const entity = {
      ...generationJobEntity,
      ...values,
      artifact: designArtifacts.find(it => it.id.toString() === values.artifact?.toString()),
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
          startedAt: displayDefaultDateTime(),
          finishedAt: displayDefaultDateTime(),
        }
      : {
          jobType: 'AI_CHAT',
          status: 'PENDING',
          ...generationJobEntity,
          createdAt: convertDateTimeFromServer(generationJobEntity.createdAt),
          startedAt: convertDateTimeFromServer(generationJobEntity.startedAt),
          finishedAt: convertDateTimeFromServer(generationJobEntity.finishedAt),
          artifact: generationJobEntity?.artifact?.id,
          session: generationJobEntity?.session?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.generationJob.home.createOrEditLabel" data-cy="GenerationJobCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.generationJob.home.createOrEditLabel">
              Create or edit a GenerationJob
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
                  id="generation-job-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.generationJob.jobType')}
                id="generation-job-jobType"
                name="jobType"
                data-cy="jobType"
                type="select"
              >
                {generationJobTypeValues.map(generationJobType => (
                  <option value={generationJobType} key={generationJobType}>
                    {translate(`kalitronFurnitureStudioApp.GenerationJobType.${generationJobType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.generationJob.status')}
                id="generation-job-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {generationJobStatusValues.map(generationJobStatus => (
                  <option value={generationJobStatus} key={generationJobStatus}>
                    {translate(`kalitronFurnitureStudioApp.GenerationJobStatus.${generationJobStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.generationJob.inputJson')}
                id="generation-job-inputJson"
                name="inputJson"
                data-cy="inputJson"
                type="textarea"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.generationJob.outputJson')}
                id="generation-job-outputJson"
                name="outputJson"
                data-cy="outputJson"
                type="textarea"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.generationJob.errorMessage')}
                id="generation-job-errorMessage"
                name="errorMessage"
                data-cy="errorMessage"
                type="textarea"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.generationJob.createdAt')}
                id="generation-job-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.generationJob.startedAt')}
                id="generation-job-startedAt"
                name="startedAt"
                data-cy="startedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.generationJob.finishedAt')}
                id="generation-job-finishedAt"
                name="finishedAt"
                data-cy="finishedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="generation-job-artifact"
                name="artifact"
                data-cy="artifact"
                label={translate('kalitronFurnitureStudioApp.generationJob.artifact')}
                type="select"
              >
                <option value="" key="0" />
                {designArtifacts
                  ? designArtifacts.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.fileName}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="generation-job-session"
                name="session"
                data-cy="session"
                label={translate('kalitronFurnitureStudioApp.generationJob.session')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/generation-job" replace variant="info">
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

export default GenerationJobUpdate;
