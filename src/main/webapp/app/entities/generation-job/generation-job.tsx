import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './generation-job.reducer';

export const GenerationJob = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const generationJobList = useAppSelector(state => state.generationJob.entities);
  const loading = useAppSelector(state => state.generationJob.loading);
  const totalItems = useAppSelector(state => state.generationJob.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="generation-job-heading" data-cy="GenerationJobHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.generationJob.home.title">Generation Jobs</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.generationJob.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/generation-job/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.generationJob.home.createLabel">Create new Generation Job</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {generationJobList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.generationJob.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('jobType')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.generationJob.jobType">Job Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('jobType')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.generationJob.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('inputJson')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.generationJob.inputJson">Input Json</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('inputJson')} />
                </th>
                <th className="hand" onClick={sort('outputJson')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.generationJob.outputJson">Output Json</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('outputJson')} />
                </th>
                <th className="hand" onClick={sort('errorMessage')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.generationJob.errorMessage">Error Message</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('errorMessage')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.generationJob.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('startedAt')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.generationJob.startedAt">Started At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('startedAt')} />
                </th>
                <th className="hand" onClick={sort('finishedAt')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.generationJob.finishedAt">Finished At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('finishedAt')} />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.generationJob.artifact">Artifact</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.generationJob.session">Session</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {generationJobList.map(generationJob => (
                <tr key={`entity-${generationJob.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/generation-job/${generationJob.id}`} variant="link" size="sm">
                      {generationJob.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.GenerationJobType.${generationJob.jobType}`} />
                  </td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.GenerationJobStatus.${generationJob.status}`} />
                  </td>
                  <td>{generationJob.inputJson}</td>
                  <td>{generationJob.outputJson}</td>
                  <td>{generationJob.errorMessage}</td>
                  <td>
                    {generationJob.createdAt ? <TextFormat type="date" value={generationJob.createdAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {generationJob.startedAt ? <TextFormat type="date" value={generationJob.startedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {generationJob.finishedAt ? <TextFormat type="date" value={generationJob.finishedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {generationJob.artifact ? (
                      <Link to={`/design-artifact/${generationJob.artifact.id}`}>{generationJob.artifact.fileName}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {generationJob.session ? (
                      <Link to={`/design-session/${generationJob.session.id}`}>{generationJob.session.sessionCode}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/generation-job/${generationJob.id}`}
                        variant="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/generation-job/${generationJob.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/generation-job/${generationJob.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        variant="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="kalitronFurnitureStudioApp.generationJob.home.notFound">No Generation Jobs found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={generationJobList && generationJobList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default GenerationJob;
