import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { Link, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './generation-job.reducer';

export const GenerationJobDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const generationJobEntity = useAppSelector(state => state.generationJob.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="generationJobDetailsHeading">
          <Translate contentKey="kalitronFurnitureStudioApp.generationJob.detail.title">GenerationJob</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{generationJobEntity.id}</dd>
          <dt>
            <span id="jobType">
              <Translate contentKey="kalitronFurnitureStudioApp.generationJob.jobType">Job Type</Translate>
            </span>
          </dt>
          <dd>{generationJobEntity.jobType}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="kalitronFurnitureStudioApp.generationJob.status">Status</Translate>
            </span>
          </dt>
          <dd>{generationJobEntity.status}</dd>
          <dt>
            <span id="inputJson">
              <Translate contentKey="kalitronFurnitureStudioApp.generationJob.inputJson">Input Json</Translate>
            </span>
          </dt>
          <dd>{generationJobEntity.inputJson}</dd>
          <dt>
            <span id="outputJson">
              <Translate contentKey="kalitronFurnitureStudioApp.generationJob.outputJson">Output Json</Translate>
            </span>
          </dt>
          <dd>{generationJobEntity.outputJson}</dd>
          <dt>
            <span id="errorMessage">
              <Translate contentKey="kalitronFurnitureStudioApp.generationJob.errorMessage">Error Message</Translate>
            </span>
          </dt>
          <dd>{generationJobEntity.errorMessage}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="kalitronFurnitureStudioApp.generationJob.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {generationJobEntity.createdAt ? (
              <TextFormat value={generationJobEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="startedAt">
              <Translate contentKey="kalitronFurnitureStudioApp.generationJob.startedAt">Started At</Translate>
            </span>
          </dt>
          <dd>
            {generationJobEntity.startedAt ? (
              <TextFormat value={generationJobEntity.startedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="finishedAt">
              <Translate contentKey="kalitronFurnitureStudioApp.generationJob.finishedAt">Finished At</Translate>
            </span>
          </dt>
          <dd>
            {generationJobEntity.finishedAt ? (
              <TextFormat value={generationJobEntity.finishedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.generationJob.artifact">Artifact</Translate>
          </dt>
          <dd>{generationJobEntity.artifact ? generationJobEntity.artifact.fileName : ''}</dd>
          <dt>
            <Translate contentKey="kalitronFurnitureStudioApp.generationJob.session">Session</Translate>
          </dt>
          <dd>{generationJobEntity.session ? generationJobEntity.session.sessionCode : ''}</dd>
        </dl>
        <Button as={Link as any} to="/generation-job" replace variant="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button as={Link as any} to={`/generation-job/${generationJobEntity.id}/edit`} replace variant="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default GenerationJobDetail;
